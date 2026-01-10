package net.thedustbuster.cee.server.commands;

import carpet.utils.CommandHelper;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.thedustbuster.cee.server.CarpetExtraExtrasServer;
import net.thedustbuster.cee.server.CarpetExtraExtrasSettings;
import net.thedustbuster.libs.core.classloading.LoadAtRuntime;

import static net.minecraft.commands.Commands.literal;
import static net.thedustbuster.cee.server.adaptors.minecraft.text.TextBuffer.text;
import static net.thedustbuster.libs.func.option.Option.Option;

@LoadAtRuntime
public final class PingCommand implements CEE_Command {
  public static final PingCommand INSTANCE = new PingCommand();
  private PingCommand() { }

  static {
    CarpetExtraExtrasServer.registerCommand(INSTANCE);
  }

  @Override
  public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
    dispatcher.register(literal("ping")
      .requires(player -> CommandHelper.canUseCommand(player, CarpetExtraExtrasSettings.commandPing))
      .executes(c -> executeCommand(c.getSource()))
    );
  }

  private int executeCommand(CommandSourceStack context) {
    Option(context.getPlayer()).whenDefined(player -> {
      int ping = player.connection.latency();
      context.sendSuccess(() -> text("Your ping: " + ping + " ms"), false);
    });

    return 1;
  }
}
