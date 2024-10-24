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
import net.thedustbuster.CarpetExtraExtrasSettings;
import net.thedustbuster.util.Attempt;
import net.thedustbuster.util.Logger;
import net.thedustbuster.util.Tuple;
import net.thedustbuster.util.Unit;
import net.thedustbuster.util.option.Option;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static net.minecraft.commands.Commands.literal;
import static net.thedustbuster.util.Unit.Unit;

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
      return new Tuple<>(player, Option.of(playerDataMap.get(player.getUUID())));
    }).map(tuple -> tuple._2().fold(
      data -> restorePlayerState(tuple._1(), data),
      () -> enterSpectatorMode(tuple._1())
    )).getOrHandle(this::handleExecutionError);
  }

  private Unit enterSpectatorMode(ServerPlayer player) {
    PreSpectatePlayerData data = new PreSpectatePlayerData(
      player.getUUID(),
      player.gameMode.getGameModeForPlayer(),
      player.position(),
      player.getRotationVector(),
      (ServerLevel) player.level()
    );

    playerDataMap.put(player.getUUID(), data);
    player.setGameMode(GameType.SPECTATOR);

    return Unit;
  }

  private Unit restorePlayerState(ServerPlayer player, PreSpectatePlayerData data) {
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

    playerDataMap.remove(player.getUUID());

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

  private record PreSpectatePlayerData(UUID id, GameType originalGamemode, Vec3 position, Vec2 rotation, ServerLevel level) { }
}

