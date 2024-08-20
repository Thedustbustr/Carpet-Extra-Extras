package net.thedustbuster.rules;

import net.minecraft.server.level.ServerPlayer;

public interface CarpetExtraExtrasRule {
  default void onTick() { }

  default void onGameStarted() { }

  default void onPlayerLoggedIn(ServerPlayer player) { }

  default void onPlayerLoggedOut(ServerPlayer player) { }
}
