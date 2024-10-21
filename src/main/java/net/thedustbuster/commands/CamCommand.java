package net.thedustbuster.commands;

import carpet.utils.CommandHelper;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Relative;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.thedustbuster.CarpetExtraExtrasServer;
import net.thedustbuster.CarpetExtraExtrasSettings;
import net.thedustbuster.util.Attempt;
import net.thedustbuster.util.Either;
import net.thedustbuster.util.Tuple;
import net.thedustbuster.util.Unit;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static net.minecraft.commands.Commands.literal;

public final class CamCommand implements Command {
  public static final CamCommand INSTANCE = new CamCommand();
  private final Map<UUID, PreSpectatePlayerData> playerDataMap = new HashMap<>();

  @Override
  public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
    final LiteralCommandNode<CommandSourceStack> camCommand = dispatcher.register(literal("cam")
      .requires(player -> CommandHelper.canUseCommand(player, CarpetExtraExtrasSettings.commandCam))
      .executes(c -> {
        executeCommand(c.getSource());
        return 1;
      })
    );

    // Alias for "cam" command
    dispatcher.register(literal("c")
      .requires(player -> CommandHelper.canUseCommand(player, CarpetExtraExtrasSettings.commandCam))
      .executes(c -> dispatcher.execute(camCommand.getLiteral(), c.getSource()))); // Manual call because .redirect() will not work
  }

  private void executeCommand(CommandSourceStack context) {
    Attempt.create(() -> {
      ServerPlayer player = context.getPlayerOrException();
      return new Tuple<>(player, findPlayerData(player.getUUID()));
    }).map(tuple -> {
      ServerPlayer player = tuple._1();
      Either<String, PreSpectatePlayerData> either = tuple._2();

      return either.fold(
        l -> {
          enterSpectatorMode(player);
          return Unit.INSTANCE;
        },
        data -> {
          restorePlayerState(player, data);
          return Unit.INSTANCE;
        }
      );
    }).getOrHandle(e -> {
      handleExecutionError(e);
      return Unit.INSTANCE;
    });
  }

  private void enterSpectatorMode(ServerPlayer player) {
    PreSpectatePlayerData data = new PreSpectatePlayerData(
      player.getUUID(),
      player.gameMode.getGameModeForPlayer(),
      player.position(),
      player.getRotationVector(),
      (ServerLevel) player.level()
    );

    playerDataMap.put(player.getUUID(), data);
    player.setGameMode(GameType.SPECTATOR);
  }

  private void restorePlayerState(ServerPlayer player, PreSpectatePlayerData data) {
    player.setGameMode(data.originalGamemode());
    player.teleportTo(
      data.level(),
      data.position().x(),
      data.position().y(),
      data.position().z(),
      EnumSet.noneOf(Relative.class),
      data.rotation().y,
      data.rotation().x,
      true
    );

    playerDataMap.remove(player.getUUID()); // Remove data once restored
  }

  private Either<String, PreSpectatePlayerData> findPlayerData(UUID playerId) {
    PreSpectatePlayerData data = playerDataMap.get(playerId);
    return (data != null) ? Either.right(data) : Either.left("Player not found");
  }

  private void handleExecutionError(Throwable e) {
    if (e instanceof CommandSyntaxException) {
      CarpetExtraExtrasServer.LOGGER.warn("CommandSyntaxException: {}", e.getMessage());
    } else {
      CarpetExtraExtrasServer.LOGGER.error("Unexpected error during command execution", e);
    }
  }

  private record PreSpectatePlayerData(UUID id, GameType originalGamemode, Vec3 position, Vec2 rotation, ServerLevel level) { }
}
