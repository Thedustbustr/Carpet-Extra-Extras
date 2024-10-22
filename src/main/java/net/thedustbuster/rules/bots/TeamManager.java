package net.thedustbuster.rules.bots;

import carpet.CarpetServer;
import carpet.patches.EntityPlayerMPFake;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import net.thedustbuster.CarpetExtraExtrasSettings;
import net.thedustbuster.util.option.Option;

public final class TeamManager {
  private static final Scoreboard SCOREBOARD = Option.of(CarpetServer.minecraft_server)
    .map(MinecraftServer::getScoreboard)
    .orElseThrow(() -> new IllegalStateException("Minecraft server is not ready"));

  private static Option<PlayerTeam> team = Option.empty();

  public static Scoreboard getScoreboard() { return SCOREBOARD; }

  private static void createTeam() {
    team = Option.of(SCOREBOARD.addPlayerTeam(CarpetExtraExtrasSettings.carpetBotTeamName));
    updateTeamProperties(team.get());
  }

  private static void findTeamOrCreate() {
    team = Option.of(SCOREBOARD.getPlayerTeam(CarpetExtraExtrasSettings.carpetBotTeamName));
    if (team.isEmpty()) createTeam();
    else updateTeamProperties(team.get());
  }

  private static void updateTeamProperties(PlayerTeam team) {
    team.setPlayerPrefix(Component.literal(CarpetExtraExtrasSettings.carpetBotTeamPrefix + " ").withStyle(CarpetExtraExtrasSettings.carpetBotTeamPrefixColor));
    team.setColor(CarpetExtraExtrasSettings.carpetBotTeamColor);
  }

  private static void updatePlayers() {
    CarpetServer.minecraft_server.getAllLevels().forEach(l -> l.players().forEach(p -> {
      if (p instanceof EntityPlayerMPFake) addPlayerToTeam(p);
    }));
  }

  public static void updateTeam() {
    updateTeam(CarpetExtraExtrasSettings.carpetBotTeam);
  }

  public static void updateTeam(boolean carpetBotPrefix) {
    findTeamOrCreate(); updatePlayers();
    if (!carpetBotPrefix && team.isDefined()) {
      SCOREBOARD.removePlayerTeam(team.get());
    }
  }

  public static void addPlayerToTeam(ServerPlayer player) {
    if (team.isEmpty()) { findTeamOrCreate(); addPlayerToTeam(player); }

    SCOREBOARD.addPlayerToTeam(player.getScoreboardName(), team.get());
  }
}
