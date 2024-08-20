package net.thedustbuster;

import carpet.CarpetServer;
import carpet.api.settings.CarpetRule;
import carpet.api.settings.Rule;
import carpet.api.settings.Validator;
import net.minecraft.commands.CommandSourceStack;
import net.thedustbuster.rules.bots.TeamManager;

public class CarpetExtraExtrasSettings {
  public static final String MOD = "CarpetExtraExtras";
  public static final String FEATURE = "Feature";
  public static final String VANILLA = "Vanilla";

  public static class carpetBotPrefixValidator extends Validator<Boolean> {
    @Override
    public Boolean validate(CommandSourceStack source, CarpetRule<Boolean> changingRule, Boolean newValue, String userInput) {
      if (CarpetServer.minecraft_server != null && CarpetServer.minecraft_server.isReady()) {
        TeamManager.updateTeam(newValue);
      }

      return newValue;
    }
  }

  @Rule(categories = {VANILLA, MOD})
  public static boolean carpetBotsSkipNight = false;

  @Rule(categories = {VANILLA, MOD}, validators = carpetBotPrefixValidator.class)
  public static boolean carpetBotPrefix = false;

  @Rule(categories = {FEATURE, MOD}, validators = carpetBotPrefixValidator.class)
  public static boolean betterEnderPearlChunkLoading = false;
}
