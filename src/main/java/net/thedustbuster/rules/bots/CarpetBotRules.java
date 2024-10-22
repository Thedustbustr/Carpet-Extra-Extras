package net.thedustbuster.rules.bots;

import carpet.CarpetServer;
import carpet.patches.EntityPlayerMPFake;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.thedustbuster.CarpetExtraExtrasSettings;
import net.thedustbuster.adaptors.carpet.LoggerHelper;
import net.thedustbuster.rules.CarpetExtraExtrasRule;
import net.thedustbuster.util.TextBuilder;

import java.util.List;

public class CarpetBotRules implements CarpetExtraExtrasRule {
  public static final CarpetBotRules INSTANCE = new CarpetBotRules();
  private static int bots = 0;

  public static int getBots() { return bots; }

  @Override
  public void onPlayerLoggedIn(ServerPlayer player) {
    if (player instanceof EntityPlayerMPFake) {
      bots++;

      if (CarpetExtraExtrasSettings.carpetBotTeam) {
        TeamManager.addPlayerToTeam(player);
      }
    }
  }

  @Override
  public void onPlayerLoggedOut(ServerPlayer player) {
    if (player instanceof EntityPlayerMPFake) {
      bots--;
    }
  }

  public Component[] createHUD() {
    TextBuilder text = new TextBuilder()
            .addText("Players: ", List.of(ChatFormatting.WHITE))
            .addText(String.valueOf(CarpetServer.minecraft_server.getPlayerCount() - CarpetBotRules.getBots()), List.of(ChatFormatting.DARK_GREEN))
            .addText(" Bots: ", List.of(ChatFormatting.WHITE))
            .addText(String.valueOf(CarpetBotRules.getBots()), List.of(ChatFormatting.GOLD));

    return LoggerHelper.createText(text);
  }
}