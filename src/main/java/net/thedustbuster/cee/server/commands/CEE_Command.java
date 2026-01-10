package net.thedustbuster.cee.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;

import static net.thedustbuster.cee.server.CarpetExtraExtrasServer.getMinecraftServer;

public interface CEE_Command {
  void register(CommandDispatcher<CommandSourceStack> dispatcher);

  static void updatePlayers() {
    getMinecraftServer().whenDefined(s -> s.getPlayerList().getPlayers().forEach(p -> s.getCommands().sendCommands(p)));
  }
}