package net.thedustbuster.cee.server.rules;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.PositionMoveRotation;
import net.minecraft.world.entity.Relative;
import net.minecraft.world.level.block.entity.BlockEntityTypes;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.thedustbuster.cee.mixins.server.ServerGamePacketListenerImplAccessor;
import net.thedustbuster.cee.server.CarpetExtraExtrasServer;
import net.thedustbuster.cee.server.CarpetExtraExtrasSettings;
import net.thedustbuster.libs.core.classloading.LoadAtRuntime;
import net.thedustbuster.libs.func.option.Option;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static net.thedustbuster.libs.func.option.Option.Option;

@LoadAtRuntime
public final class PistonPushDesyncFix implements CEE_Rule {
  private static final PistonPushDesyncFix INSTANCE = new PistonPushDesyncFix();
  private PistonPushDesyncFix() { }

  static {
    CarpetExtraExtrasServer.registerRule(INSTANCE);
  }

  private static final int TRACKING_TICKS = 20;
  private static final int PUSH_SYNC_TICKS = 2;
  private static final int PUSH_HISTORY_TICKS = 8;
  private static final double CLEARANCE = 0.01;
  private static final double MIN_CLIP_DEPTH = 0.05;
  private static final double MAX_CORRECTION = 2.50 + CLEARANCE;
  private static final double MAX_CORRECTABLE_LAG = 4.0;

  private static final double PUSH_STROKE_LENGTH = 1.0;

  private static final Map<UUID, PushData> trackedPlayers = new HashMap<>();

  @Override
  public void onServerLoadedCleanup() {
    trackedPlayers.clear();
  }

  @Override
  public void onPlayerLoggedOut(ServerPlayer player) {
    trackedPlayers.remove(player.getUUID());
  }

  @Override
  public void onTick() {
    if (trackedPlayers.isEmpty() || !CarpetExtraExtrasSettings.pistonPushDesyncFix) return;

    CarpetExtraExtrasServer.getMinecraftServer().whenDefined(server -> {
      int tick = server.getTickCount();
      trackedPlayers.values().removeIf(data -> data.isExpired(tick));

      PlayerList players = server.getPlayerList();
      trackedPlayers.forEach((uuid, data) -> {
        Option(players.getPlayer(uuid)).whenDefined(player -> correctPlayerPosIfClipped(player, data));
        data.advanceHistory();
      });
    });
  }

  public static void recordPushEvent(ServerPlayer player, Direction direction, double distance) {
    trackedPlayers
      .computeIfAbsent(player.getUUID(), _ -> new PushData(direction))
      .recordPushEvent(direction, distance, player.level().getServer().getTickCount());
  }

  public static boolean handleRejectedPos(ServerPlayer player, Vec3 serverPos, Vec3 clientPos, float yRot, float xRot) {
    return Option(trackedPlayers.get(player.getUUID()))
      .map(data -> handleRejectedPos(player, data, serverPos, clientPos, yRot, xRot))
      .getOrElse(false);
  }

  private static boolean handleRejectedPos(ServerPlayer player, PushData data, Vec3 serverPos, Vec3 clientPos, float yRot, float xRot) {
    final double minCorrectableLag = 1.0E-4;
    final double notClipped = 0.0;

    Direction.Axis axis = data.direction.getAxis();
    double lag = (serverPos.get(axis) - clientPos.get(axis)) * data.direction.getAxisDirection().getStep();
    if (lag <= minCorrectableLag || lag > MAX_CORRECTABLE_LAG) return false;

    // Stay where the server's push left the player; a lagging client catches up on its own
    player.absSnapTo(serverPos.x, serverPos.y, serverPos.z, yRot, xRot);

    boolean missedPush = missedPushEvent(player, data, serverPos, clientPos, lag);
    if (missedPush) correctPlayerPos(player, data, notClipped);
    return true;
  }

  private static boolean missedPushEvent(ServerPlayer player, PushData data, Vec3 serverPos, Vec3 clientPos, double lag) {
    final int millisPerTick = 50;

    EntityDimensions dimensions = player.getDimensions(player.getPose());
    AABB clientBox = dimensions.makeBoundingBox(clientPos);

    // Ticks of pushes the client may not have applied yet, based on the client's latency
    int syncTicks = Math.min(PUSH_SYNC_TICKS + player.connection.latency() / millisPerTick, PUSH_HISTORY_TICKS);

    // How far the pushing blocks have moved: this push if they are still moving, otherwise over those recent ticks
    double travelled = remainingPushTravelDistance(player, clientBox, data.direction)
      .map(remaining -> PUSH_STROKE_LENGTH - remaining)
      .getOrElse(data.recentPushDistance(syncTicks));

    // The client's box at the server position on the push axis only
    Direction.Axis axis = data.direction.getAxis();
    AABB serverBox = dimensions.makeBoundingBox(clientPos.with(axis, serverPos.get(axis)));

    // The space between where the client would be after those pushes and the server position, never beyond it
    AABB path = clientBox.move(Vec3.ZERO.relative(data.direction, Math.min(travelled, lag))).minmax(serverBox);
    return calculatePlayerClipDepth(player, path, data.direction) >= MIN_CLIP_DEPTH;
  }

  private static double calculatePlayerClipDepth(ServerPlayer player, AABB box, Direction direction) {
    final double collisionEpsilon = 1.0E-5;

    Direction.Axis axis = direction.getAxis();
    boolean positive = direction.getAxisDirection() == Direction.AxisDirection.POSITIVE;

    double depth = 0.0;
    for (VoxelShape shape : player.level().getBlockCollisions(player, box.deflate(collisionEpsilon))) {
      double overlap = positive ? shape.max(axis) - box.min(axis) : box.max(axis) - shape.min(axis);
      depth = Math.max(depth, overlap);
    }

    return depth;
  }

  private static Option<Double> remainingPushTravelDistance(ServerPlayer player, AABB box, Direction direction) {
    final double searchMargin = 0.05;
    final float endOfTickProgress = 1.0F;

    // Includes the blocks right behind the player
    AABB area = box.expandTowards(Vec3.ZERO.relative(direction.getOpposite(), searchMargin));
    return Option.of(BlockPos.betweenClosedStream(area)
      .flatMap(pos -> player.level().getBlockEntity(pos, BlockEntityTypes.PISTON).stream())
      .filter(piston -> piston.getMovementDirection() == direction)
      .map(piston -> PUSH_STROKE_LENGTH - piston.getProgress(endOfTickProgress))
      .max(Double::compare));
  }

  private static boolean isAwaitingPosition(ServerPlayer player) {
    return Option(((ServerGamePacketListenerImplAccessor) player.connection).getAwaitingPositionFromClient()).isDefined();
  }

  private static void correctPlayerPosIfClipped(ServerPlayer player, PushData data) {
    if (!player.isAlive()
      || player.noPhysics
      || player.isPassenger()
      || player.isSleeping()
      || player.isChangingDimension()
      || isAwaitingPosition(player) // A correction is already on its way to the client
    ) return;

    double depth = calculatePlayerClipDepth(player, player.getBoundingBox(), data.direction);
    if (depth >= MIN_CLIP_DEPTH) correctPlayerPos(player, data, depth);
  }

  private static void correctPlayerPos(ServerPlayer player, PushData data, double depth) {
    AABB box = player.getBoundingBox();

    // Aim for where the pushing blocks will come to rest
    double distance = depth + remainingPushTravelDistance(player, box, data.direction).getOrElse(0.0) + CLEARANCE;
    if (distance > MAX_CORRECTION) return;

    // Only move far enough to leave the blocks the player is clipped into; if that spot is taken (e.g. crushed against a wall), do nothing
    Direction.Axis axis = data.direction.getAxis();
    double offset = distance * data.direction.getAxisDirection().getStep();
    if (!player.level().noCollision(player, box.move(Vec3.ZERO.with(axis, offset)))) return;

    // Absolute on the push axis only; velocity on that axis is zeroed
    Set<Relative> relatives = EnumSet.allOf(Relative.class);
    switch (axis) {
      case X -> relatives.removeAll(Set.of(Relative.X, Relative.DELTA_X));
      case Y -> relatives.removeAll(Set.of(Relative.Y, Relative.DELTA_Y));
      case Z -> relatives.removeAll(Set.of(Relative.Z, Relative.DELTA_Z));
    }

    // Move player
    Vec3 position = Vec3.ZERO.with(axis, player.position().get(axis) + offset);
    player.connection.teleport(new PositionMoveRotation(position, Vec3.ZERO, 0.0F, 0.0F), relatives);
  }

  private static final class PushData {
    private Direction direction;
    private int expiresAt;
    private final double[] pushHistory = new double[PUSH_HISTORY_TICKS];

    private PushData(Direction direction) {
      this.direction = direction;
    }

    private void recordPushEvent(Direction direction, double distance, int tick) {
      this.direction = direction;
      this.expiresAt = tick + TRACKING_TICKS;
      this.pushHistory[0] += distance;
    }

    private boolean isExpired(int tick) {
      return tick > expiresAt;
    }

    private double recentPushDistance(int ticks) {
      double pushed = 0.0;
      for (int i = 0; i < ticks; i++) pushed += pushHistory[i];
      return Math.min(pushed, PUSH_STROKE_LENGTH);
    }

    private void advanceHistory() {
      System.arraycopy(pushHistory, 0, pushHistory, 1, PUSH_HISTORY_TICKS - 1);
      pushHistory[0] = 0.0;
    }
  }
}
