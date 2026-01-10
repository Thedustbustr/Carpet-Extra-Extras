package net.thedustbuster.cee.server.commands;

import carpet.utils.CommandHelper;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Relative;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.thedustbuster.cee.server.CarpetExtraExtrasServer;
import net.thedustbuster.cee.server.CarpetExtraExtrasSettings;
import net.thedustbuster.cee.server.adaptors.minecraft.MessagingHelper;
import net.thedustbuster.cee.server.adaptors.minecraft.text.TextBuffer;
import net.thedustbuster.libs.core.classloading.LoadAtRuntime;
import net.thedustbuster.libs.core.tuple.Pair;
import net.thedustbuster.libs.func.Attempt;
import net.thedustbuster.libs.func.Unit;
import net.thedustbuster.libs.func.option.Option;
import net.thedustbuster.cee.server.util.Logger;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static net.minecraft.commands.Commands.literal;
import static net.thedustbuster.libs.func.Unit.Unit;

@LoadAtRuntime
public final class CamCommand implements CEE_Command {
  public static final CamCommand INSTANCE = new CamCommand();
  private CamCommand() { }

  static {
    CarpetExtraExtrasServer.registerCommand(INSTANCE);
  }

  private static final Map<UUID, FreecamData> playerDataMap = new HashMap<>();

  public static Option<FreecamData> getPlayerData(UUID id) {
    return Option.of(playerDataMap.get(id));
  }

  public static void addPlayerData(UUID id, FreecamData data) {
    playerDataMap.put(id, data);
  }

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
      return new Pair<>(player, Option.of(playerDataMap.get(player.getUUID())));
    }).map(tuple -> tuple._2().fold(
      data -> exitFreecam(tuple._1(), data),
      () -> enterFreecam(tuple._1())
    )).getOrHandle(this::handleExecutionError);
  }

  private Unit enterFreecam(ServerPlayer player) {
    FreecamData data = new FreecamData(
      player.gameMode.getGameModeForPlayer(),
      player.position(),
      player.getRotationVector(),
      player.level().dimension()
    );

    playerDataMap.put(player.getUUID(), data);
    player.setGameMode(GameType.SPECTATOR);
    MessagingHelper.sendActionBarMessage(player,
      new TextBuffer()
        .addText("Gamemode: ", ChatFormatting.GOLD)
        .addText("SPECTATOR", ChatFormatting.WHITE)
        .build()
    );

    return Unit;
  }

  private Unit exitFreecam(ServerPlayer player, FreecamData data) {
    MinecraftServer server = CarpetExtraExtrasServer.getMinecraftServer()
      .getOrThrow(() -> new IllegalStateException("Minecraft Server is not ready"));

    player.setGameMode(data.gamemode());
    player.teleportTo(
      server.getLevel(data.level()),
      data.position().x(),
      data.position().y(),
      data.position().z(),
      EnumSet.noneOf(Relative.class),
      data.rotation().y,
      data.rotation().x,
      true
    );

    playerDataMap.remove(player.getUUID());
    MessagingHelper.sendActionBarMessage(player,
      new TextBuffer()
        .addText("Gamemode: ", ChatFormatting.GOLD)
        .addText(data.gamemode.getName().toUpperCase(), ChatFormatting.WHITE)
        .build()
    );
    return Unit;
  }

  private Unit handleExecutionError(Throwable e) {
    if (e instanceof CommandSyntaxException) {
      Logger.warn("CommandSyntaxException: " + e.getMessage());
    } else {
      Logger.error("Unexpected error during command execution" + e);
    }

    return Unit;
  }

  public record FreecamData(GameType gamemode, Vec3 position, Vec2 rotation, ResourceKey<Level> level) {
    public static final Codec<FreecamData> CODEC =
      RecordCodecBuilder.create(instance ->
        instance.group(
          GameType.CODEC.fieldOf("gamemode").forGetter(FreecamData::gamemode),
          Vec3.CODEC.fieldOf("position").forGetter(FreecamData::position),
          Vec2.CODEC.fieldOf("rotation").forGetter(FreecamData::rotation),
          ResourceKey.codec(Registries.DIMENSION).fieldOf("level").forGetter(FreecamData::level)
        ).apply(instance, FreecamData::new)
      );
  }
}