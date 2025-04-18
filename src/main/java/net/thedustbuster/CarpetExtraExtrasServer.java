package net.thedustbuster;

import carpet.CarpetExtension;
import carpet.CarpetServer;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.api.ModInitializer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.thedustbuster.commands.CEE_Command;
import net.thedustbuster.commands.CamCommand;
import net.thedustbuster.rules.CEE_Rule;
import net.thedustbuster.rules.CarpetBotTeam;
import net.thedustbuster.rules.PearlTracking;
import net.thedustbuster.util.TickDelayManager;
import net.thedustbuster.util.option.Option;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public final class CarpetExtraExtrasServer implements CarpetExtension, ModInitializer {
  public static final Logger LOGGER = LoggerFactory.getLogger("carpet-extra-extras");
  private static final List<CEE_Rule> rules = new ArrayList<>();
  private static final List<CEE_Command> commands = new ArrayList<>();

  private static void registerRule(CEE_Rule c) { rules.add(c); }
  private static void registerCommand(CEE_Command c) { commands.add(c); }

  public static Option<MinecraftServer> getMinecraftServer() {
    return Option.of(CarpetServer.minecraft_server)
      .filter(MinecraftServer::isReady);
  }

  @Override
  public String version() {
    return "carpet-extra-extras";
  }

  @Override
  public void onInitialize() {
    CarpetServer.manageExtension(this);

    /* Register Rules */
    registerRule(CarpetBotTeam.INSTANCE);
    registerRule(PearlTracking.INSTANCE);
  }

  @Override
  public void onServerLoaded(MinecraftServer server) {
    /* Register Commands */
    registerCommand(CamCommand.INSTANCE);

    commands.forEach(c -> c.register(server.getCommands().getDispatcher()));
  }

  @Override
  public void onTick(MinecraftServer server) {
    rules.forEach(CEE_Rule::onTick);

    TickDelayManager.tick();
  }

  @Override
  public void onPlayerLoggedIn(ServerPlayer player) {
    rules.forEach(rule -> rule.onPlayerLoggedIn(player));
  }

  @Override
  public void onPlayerLoggedOut(ServerPlayer player) {
    rules.forEach(rule -> rule.onPlayerLoggedOut(player));
  }

  @Override
  public void onGameStarted() {
    CarpetServer.settingsManager.parseSettingsClass(CarpetExtraExtrasSettings.class);
    rules.forEach(CEE_Rule::onGameStarted);
  }

  /* Taken from https://github.com/gnembon/carpet-extra/blob/master/src/main/java/carpetextra/utils/CarpetExtraTranslations.java# */
  @Override
  public Map<String, String> canHasTranslations(String lang) {
    InputStream langFile = CarpetExtraExtrasServer.class.getClassLoader().getResourceAsStream("assets/carpet-extra-extras/lang/%s.json".formatted(lang));
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