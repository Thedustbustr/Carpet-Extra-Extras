package net.thedustbuster;

import carpet.CarpetExtension;
import carpet.CarpetServer;
import carpet.patches.EntityPlayerMPFake;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.api.ModInitializer;
import net.minecraft.server.level.ServerPlayer;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;

public final class BetterCarpetBots implements CarpetExtension, ModInitializer {
  public static final Logger LOGGER = LoggerFactory.getLogger("better-carpet-bots");

  private static int bots = 0;

  public static int getBots() { return bots; }

  @Override
  public String version() {
    return "better-carpet-bots";
  }


  @Override
  public void onInitialize() {
    LOGGER.info("Loading extension"); CarpetServer.manageExtension(this);
  }

  @Override
  public void onGameStarted() {
    CarpetServer.settingsManager.parseSettingsClass(BetterCarpetBotsSettings.class);
  }

  @Override
  public void onPlayerLoggedIn(ServerPlayer player) {
    if (player instanceof EntityPlayerMPFake) {
      bots++;

      if (BetterCarpetBotsSettings.carpetBotPrefix) { TeamManager.addPlayerToTeam(player); }
    }
  }

  @Override
  public void onPlayerLoggedOut(ServerPlayer player) {
    if (player instanceof EntityPlayerMPFake) {
      bots--;
    }
  }

  @Override
  public Map<String, String> canHasTranslations(String lang) {
    InputStream langFile = BetterCarpetBots.class.getClassLoader().getResourceAsStream("assets/better-carpet-bots/lang/%s.json".formatted(lang));
    if (langFile == null) {
      return Collections.emptyMap();
    } String jsonData; try {
      jsonData = IOUtils.toString(langFile, StandardCharsets.UTF_8);
    } catch (IOException e) {
      return Collections.emptyMap();
    } Gson gson = new GsonBuilder().setLenient().create(); // lenient allows for comments
    return gson.fromJson(jsonData, new TypeToken<Map<String, String>>() { }.getType());
  }
}