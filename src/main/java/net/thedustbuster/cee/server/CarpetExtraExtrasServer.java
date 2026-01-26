package net.thedustbuster.cee.server;

import carpet.CarpetExtension;
import carpet.CarpetServer;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.api.ModInitializer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.thedustbuster.cee.server.commands.CEE_Command;
import net.thedustbuster.cee.server.rules.CEE_Rule;
import net.thedustbuster.cee.server.util.Logger;
import net.thedustbuster.cee.server.util.TickDelayManager;
import net.thedustbuster.libs.core.classloading.ClassLoader;
import net.thedustbuster.libs.func.option.Option;
import org.apache.commons.io.IOUtils;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static net.thedustbuster.libs.func.Unit.Unit;

public final class CarpetExtraExtrasServer implements CarpetExtension, ModInitializer {
  public static final String MOD_ID = "carpet-extra-extras";
  public static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

  private static final List<CEE_Rule> rules = new ArrayList<>();
  private static final List<CEE_Command> commands = new ArrayList<>();
  public static void registerRule(CEE_Rule r) { rules.add(r); }
  public static void registerCommand(CEE_Command c) { commands.add(c); }

  private static Option<MinecraftServer> getMinecraftServerUnsafe() {
    return Option.of(CarpetServer.minecraft_server);
  }
  public static Option<MinecraftServer> getMinecraftServer() {
    return getMinecraftServerUnsafe().filter(MinecraftServer::isReady);
  }

  public static void runOnServerThread(Consumer<MinecraftServer> r) {
    getMinecraftServer().fold(s -> Unit(() -> s.execute(() -> r.accept(s))), () -> Logger.warn("Attempted to run on a non-ready server thread."));
  }

  // This should only be used for tasks that don't require the server to be entirely loaded (NOT worldgen)
  public static void runOnServerThreadUnsafe(Consumer<MinecraftServer> r) {
    getMinecraftServerUnsafe().fold(s -> Unit(() -> s.execute(() -> r.accept(s))), () -> Logger.warn("Attempted to run on a non-existent server thread."));
  }

  public static void reloadCommands() {
    runOnServerThreadUnsafe(server -> {
      /* Register commands */
      commands.forEach(c -> c.register(server.getCommands().getDispatcher()));

      /* Provide commands to players */
      server.getPlayerList().getPlayers().forEach(p -> server.getCommands().sendCommands(p));
    });
  }

  @Override
  public void onInitialize() {
    CarpetServer.manageExtension(this);

    /* Load rules and commands */
    new ClassLoader("net.thedustbuster.cee.server.rules").load();
    new ClassLoader("net.thedustbuster.cee.server.commands").load();
  }

  @Override
  public void onServerLoaded(MinecraftServer server) {
    /* Register commands */
    reloadCommands();
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
  }

  /* Taken from https://github.com/gnembon/carpet-extra/blob/master/src/main/java/carpetextra/utils/CarpetExtraTranslations.java# */
  @Override
  public Map<String, String> canHasTranslations(String lang) {
    InputStream langFile = CarpetExtraExtrasServer.class.getClassLoader().getResourceAsStream("assets/carpet-extra-extras/lang/%s.json".formatted(lang));
    if (langFile == null) {
      return Collections.emptyMap();
    }
    String jsonData;
    try {
      jsonData = IOUtils.toString(langFile, StandardCharsets.UTF_8);
    } catch (IOException e) {
      return Collections.emptyMap();
    }
    Gson gson = new GsonBuilder().create();
    return gson.fromJson(jsonData, new TypeToken<Map<String, String>>() { }.getType());
  }
}