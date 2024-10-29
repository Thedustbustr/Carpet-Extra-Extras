package net.thedustbuster.rules;

import carpet.CarpetServer;
import carpet.patches.EntityPlayerMPFake;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import net.thedustbuster.CarpetExtraExtrasSettings;
import net.thedustbuster.adaptors.carpet.LoggerHelper;
import net.thedustbuster.util.TextBuilder;
import net.thedustbuster.util.option.Option;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public final class CarpetBotTeam implements CEE_Rule {
  public static final CarpetBotTeam INSTANCE = new CarpetBotTeam();

  public static int getBots() {
    return getBotPlayers().size();
  }

  private static Scoreboard getScoreboard() {
    return Option.of(CarpetServer.minecraft_server)
      .map(MinecraftServer::getScoreboard)
      .orElseThrow(() -> new IllegalStateException("Minecraft Server is not ready"));
  }

  private static Option<PlayerTeam> team = Option.empty();

  @Override
  public void onPlayerLoggedIn(ServerPlayer player) {
    if (player instanceof EntityPlayerMPFake) updateTeam();
  }

  @Override
  public void onPlayerLoggedOut(ServerPlayer player) {
    if (player instanceof EntityPlayerMPFake) updateTeam();
  }

  public Component[] createHUD() {
    TextBuilder text = new TextBuilder()
      .addText("Players: ", List.of(ChatFormatting.WHITE))
      .addText(String.valueOf(CarpetServer.minecraft_server.getPlayerCount() - getBots()), List.of(ChatFormatting.DARK_GREEN))
      .addText(" Bots: ", List.of(ChatFormatting.WHITE))
      .addText(String.valueOf(getBots()), List.of(ChatFormatting.GOLD));

    return LoggerHelper.createText(text);
  }

  // ###################### [ Team ] ###################### \\
  public static void updateTeam() {
    updateTeam(CarpetExtraExtrasSettings.carpetBotTeam);
  }

  public static void updateTeam(boolean carpetBotTeam) {
    team = Option.of(getScoreboard().getPlayerTeam(CarpetExtraExtrasSettings.carpetBotTeamName));

    if (carpetBotTeam) {
      team.whenEmpty(CarpetBotTeam::createTeam);
      updatePlayers();
    } else {
      team.whenDefined(t -> getScoreboard().removePlayerTeam(t));
    }
  }

  private static void createTeam() {
    team = Option.of(getScoreboard().addPlayerTeam(CarpetExtraExtrasSettings.carpetBotTeamName));
    team.whenDefined(CarpetBotTeam::updateTeamProperties);
  }

  private static void updateTeamProperties(PlayerTeam team) {
    team.setPlayerPrefix(Component.literal(CarpetExtraExtrasSettings.carpetBotTeamPrefix + " ").withStyle(CarpetExtraExtrasSettings.carpetBotTeamPrefixColor));
    team.setColor(CarpetExtraExtrasSettings.carpetBotTeamColor);
  }

  private static void updatePlayers() {
    getBotPlayers().forEach(player ->
      team.whenDefined(t -> getScoreboard().addPlayerToTeam(player.getScoreboardName(), t)));
  }

  private static Set<ServerPlayer> getBotPlayers() {
    return Option.of(CarpetServer.minecraft_server)
      .map(server -> StreamSupport.stream(server.getAllLevels().spliterator(), false)  // Convert Iterable to Stream
        .flatMap(level -> level.players().stream())
        .filter(player -> player instanceof EntityPlayerMPFake)
        .collect(Collectors.toSet())
      )
      .orElse(Set.of());
  }
}
