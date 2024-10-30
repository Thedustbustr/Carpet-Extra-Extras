package net.thedustbuster.adaptors.minecraft;

import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.server.level.ServerPlayer;
import net.thedustbuster.util.option.Option;

import java.util.Arrays;

public final class MessagingHelper {
  private MessagingHelper() { }

  public static void sendActionBarMessage(ServerPlayer player, Component text) {
    sendActionBarMessage(new ServerPlayer[] {player}, text);
  }

  public static void sendActionBarMessage(ServerPlayer[] players, Component text) {
    if (Option.of(text).isEmpty()) return;

    Arrays.stream(players)
      .filter(p -> Option.of(p).isDefined())
      .forEach(player -> player.connection.send(new ClientboundSetActionBarTextPacket(text)));
  }
}
