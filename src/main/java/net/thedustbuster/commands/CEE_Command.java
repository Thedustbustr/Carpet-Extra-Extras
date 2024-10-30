package net.thedustbuster.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;

import static net.thedustbuster.CarpetExtraExtrasServer.getMinecraftServer;

public interface CEE_Command {
  void register(CommandDispatcher<CommandSourceStack> dispatcher);

  static void updatePlayers() {
    getMinecraftServer().whenDefined(server -> server.getPlayerList().getPlayers().forEach(p -> server.getCommands().sendCommands(p)));
  }
}