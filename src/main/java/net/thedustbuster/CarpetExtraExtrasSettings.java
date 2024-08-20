package net.thedustbuster;

import carpet.api.settings.CarpetRule;
import carpet.api.settings.Rule;
import carpet.api.settings.Validator;
import net.minecraft.commands.CommandSourceStack;
import net.thedustbuster.rules.bots.TeamManager;

public class CarpetExtraExtrasSettings {
  public static final String MOD = "BetterCarpetBots";
  public static final String VANILLA = "vanilla";

  public static class carpetBotPrefixValidator extends Validator<Boolean> {
    @Override
    public Boolean validate(CommandSourceStack source, CarpetRule<Boolean> changingRule, Boolean newValue, String userInput) {
      TeamManager.updateTeam(newValue);
      return newValue;
    }
  }

  @Rule(categories = {VANILLA, MOD})
  public static boolean carpetBotsSkipNight = false;

  @Rule(categories = {VANILLA, MOD}, validators = carpetBotPrefixValidator.class)
  public static boolean carpetBotPrefix = false;

  @Rule(categories = {VANILLA, MOD}, validators = carpetBotPrefixValidator.class)
  public static boolean betterEnderPearlChunkLoading = false;
}
