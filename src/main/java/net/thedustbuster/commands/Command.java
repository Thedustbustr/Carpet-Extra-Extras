package net.thedustbuster.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;

public interface Command {
  void register(CommandDispatcher<CommandSourceStack> dispatcher);
}