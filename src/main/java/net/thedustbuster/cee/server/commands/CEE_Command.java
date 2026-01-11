package net.thedustbuster.cee.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;

public interface CEE_Command {
  void register(CommandDispatcher<CommandSourceStack> dispatcher);
}