package net.thedustbuster.cee.server.rules;

import carpet.CarpetServer;
import carpet.patches.EntityPlayerMPFake;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import net.thedustbuster.cee.server.CarpetExtraExtrasServer;
import net.thedustbuster.cee.server.CarpetExtraExtrasSettings;
import net.thedustbuster.cee.server.adaptors.carpet.LoggerHelper;
import net.thedustbuster.cee.server.adaptors.minecraft.text.TextBuffer;
import net.thedustbuster.libs.core.classloading.LoadAtRuntime;
import net.thedustbuster.libs.func.option.Option;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static net.thedustbuster.libs.func.option.None.None;

@LoadAtRuntime
public final class CarpetBotTeam implements CEE_Rule {
  private static final CarpetBotTeam INSTANCE = new CarpetBotTeam();
  private CarpetBotTeam() { }

  static {
    CarpetExtraExtrasServer.registerRule(INSTANCE);
  }

  @Override
  public void onPlayerLoggedIn(ServerPlayer player) {
    if (player instanceof EntityPlayerMPFake) updateTeam();
  }

  @Override
  public void onPlayerLoggedOut(ServerPlayer player) {
    if (player instanceof EntityPlayerMPFake) updateTeam();
  }

  public static Component[] createHUD() {
    TextBuffer text = new TextBuffer()
      .addText("Players: ", List.of(ChatFormatting.WHITE))
      .addText(String.valueOf(CarpetServer.minecraft_server.getPlayerCount() - getBots()), List.of(ChatFormatting.DARK_GREEN))
      .addText(" Bots: ", List.of(ChatFormatting.WHITE))
      .addText(String.valueOf(getBots()), List.of(ChatFormatting.GOLD));

    return LoggerHelper.createText(text);
  }

  // ###################### [ Team ] ###################### \\
  private static Option<PlayerTeam> team = None();
  public static int getBots() {
    return getBotPlayers().size();
  }

  public static int getActiveBots() {
    return (int) getBotPlayers().stream()
      .filter(b -> !b.isSpectator() && b.level().dimension() == ServerLevel.OVERWORLD)
      .count();
  }

  public static PlayerTeam getTeam() {
    return team.getOrElse(() -> {
      updateTeam();
      return team.getOrThrow(() -> new IllegalStateException("Failed to load carpet bot team"));
    });
  }

  private static Scoreboard getScoreboard() {
    return CarpetExtraExtrasServer.getMinecraftServer()
      .getOrThrow(() -> new IllegalStateException("Minecraft Server is not ready"))
      .getScoreboard();
  }

  public static void updateTeam() {
    updateTeam(CarpetExtraExtrasSettings.carpetBotTeam);
  }

  public static void updateTeam(boolean carpetBotTeam) {
    team = Option.of(getScoreboard().getPlayerTeam(CarpetExtraExtrasSettings.carpetBotTeamName));
    team.whenDefined(t -> getScoreboard().removePlayerTeam(t));

    if (carpetBotTeam) {
      CarpetBotTeam.createTeam();
      updatePlayers();
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
    return CarpetExtraExtrasServer.getMinecraftServer()
      .map(server -> StreamSupport.stream(server.getAllLevels().spliterator(), false)
        .flatMap(level -> level.players().stream())
        .filter(player -> player instanceof EntityPlayerMPFake)
        .collect(Collectors.toSet())
      )
      .getOrElse(Set.of());
  }
}