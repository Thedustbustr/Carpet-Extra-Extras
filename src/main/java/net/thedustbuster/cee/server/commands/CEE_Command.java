package net.thedustbuster.cee.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;

public interface CEE_Command {
  void register(CommandDispatcher<CommandSourceStack> dispatcher);

  default void onServerLoadedCleanup() { }
}