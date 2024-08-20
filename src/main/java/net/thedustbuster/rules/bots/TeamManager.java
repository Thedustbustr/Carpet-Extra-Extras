package net.thedustbuster.rules.bots;

import carpet.CarpetServer;
import carpet.patches.EntityPlayerMPFake;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import net.thedustbuster.CarpetExtraExtrasSettings;

public final class TeamManager {
  private static final Scoreboard SCOREBOARD = CarpetServer.minecraft_server.getScoreboard();

  private static PlayerTeam team;

  public static PlayerTeam getTeam() { return team; }

  public static Scoreboard getScoreboard() { return SCOREBOARD; }

  private static void createTeam() {
    team = SCOREBOARD.addPlayerTeam("bots");
    team.setPlayerPrefix(Component.literal("[Bot] ").withStyle(ChatFormatting.GOLD));
    team.setColor(ChatFormatting.GRAY);
  }

  private static void findTeam() {
    team = SCOREBOARD.getPlayerTeam("bots"); if (team == null) { createTeam(); }
  }

  private static void updatePlayers() {
    CarpetServer.minecraft_server.getAllLevels().forEach(l -> l.players().forEach(p -> {
      if (p instanceof EntityPlayerMPFake) addPlayerToTeam(p);
    }));
  }

  public static void updateTeam() {
    updateTeam(CarpetExtraExtrasSettings.carpetBotPrefix);
  }

  public static void updateTeam(boolean carpetBotPrefix) {
    findTeam(); updatePlayers();
    if (!carpetBotPrefix && team != null) {
      SCOREBOARD.removePlayerTeam(team);
    }
  }

  public static void addPlayerToTeam(ServerPlayer player) {
    if (team == null) { findTeam(); addPlayerToTeam(player); }

    SCOREBOARD.addPlayerToTeam(player.getScoreboardName(), team);
  }
}
