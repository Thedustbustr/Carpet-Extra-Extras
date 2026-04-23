package net.thedustbuster.cee.server.commands;

import carpet.utils.CommandHelper;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.protocol.common.ClientboundTransferPacket;
import net.minecraft.server.level.ServerPlayer;
import net.thedustbuster.cee.server.CarpetExtraExtrasServer;
import net.thedustbuster.cee.server.CarpetExtraExtrasSettings;
import net.thedustbuster.libs.core.classloading.LoadAtRuntime;
import net.thedustbuster.libs.func.Attempt;

import java.util.concurrent.CompletableFuture;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

@LoadAtRuntime
public final class WarpCommand implements CEE_Command {
  public static final WarpCommand INSTANCE = new WarpCommand();
  private WarpCommand() { }

  static {
    CarpetExtraExtrasServer.registerCommand(INSTANCE);
  }

  @Override
  public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
    dispatcher.register(literal("warp")
      .requires(player -> CommandHelper.canUseCommand(player, CarpetExtraExtrasSettings.commandWarp))
      .then(argument("destination", StringArgumentType.word())
        .suggests(this::suggestDestinations)
        .executes(c -> executeCommand(c.getSource(), StringArgumentType.getString(c, "destination")))
      )
    );
  }

  private int executeCommand(CommandSourceStack source, String alias) {
    return Attempt.create(() -> {
      ServerPlayer player = source.getPlayerOrException();

      CarpetExtraExtrasSettings.parseWarpDestinations().stream()
        .filter(d -> d.match((a, _, _) -> a.equals(alias))).findFirst()
        .orElseThrow(() -> new RuntimeException("Unknown warp destination: " + alias))
        .match((_, hostname, port) -> {
          player.connection.send(new ClientboundTransferPacket(hostname, port.getOrElse(25565)));
          return 1;
        });

      return 1;
    }).toOption().getOrElse(0);
  }

  private CompletableFuture<Suggestions> suggestDestinations(CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) {
    CarpetExtraExtrasSettings.parseWarpDestinations().stream()
      .map(d -> d.match((alias, _, _) -> alias))
      .forEach(builder::suggest);
    return builder.buildFuture();
  }
}