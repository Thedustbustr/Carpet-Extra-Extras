package net.thedustbuster;

import carpet.CarpetServer;
import carpet.api.settings.CarpetRule;
import carpet.api.settings.Rule;
import carpet.api.settings.Validator;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.thedustbuster.rules.CarpetBotTeam;
import net.thedustbuster.util.option.Option;
import org.jetbrains.annotations.Nullable;

public class CarpetExtraExtrasSettings {
  public static final String MOD = "CarpetExtraExtras";
  public static final String FEATURE = "Feature";
  public static final String LTS = "LTS";
  public static final String COMMAND = "Command";
  public static final String VANILLA = "Vanilla";
  public static final int MAX_TEAM_NAME_LENGTH = 64;
  public static final int MAX_TEAM_PREFIX_LENGTH = 16;

  private static void updateTeam() {
    Option.of(CarpetServer.minecraft_server)
      .whenDefined(server -> {
        if (server.isReady()) server.execute(CarpetBotTeam::updateTeam);
      });
  }

  // ###################### [ Rules ] ###################### \\
  @Rule(categories = {VANILLA, MOD})
  public static boolean trackEnderPearls = false;

  @Rule(categories = {VANILLA, MOD})
  public static boolean enderPearlChunkLoadingFix = false;

  @Rule(categories = {FEATURE, LTS, MOD})
  public static boolean Pre21ThrowableEntityBehaviorReintroduced = false;

  @Rule(categories = {FEATURE, MOD})
  public static boolean carpetBotsSkipNight = false;

  @Rule(categories = {FEATURE, MOD}, validators = CarpetBotTeamValidator.class)
  public static boolean carpetBotTeam = false;

  @Rule(categories = {FEATURE, MOD}, validators = CarpetBotTeamNameValidator.class)
  public static String carpetBotTeamName = "cee_bots";

  @Rule(categories = {FEATURE, MOD}, validators = CarpetBotTeamPrefixValidator.class)
  public static String carpetBotTeamPrefix = "[Bots]";

  @Rule(categories = {FEATURE, MOD}, validators = CarpetBotTeamColorValidator.class)
  public static ChatFormatting carpetBotTeamColor = ChatFormatting.GRAY;

  @Rule(categories = {FEATURE, MOD}, validators = CarpetBotTeamColorValidator.class)
  public static ChatFormatting carpetBotTeamPrefixColor = ChatFormatting.GOLD;

  // ###################### [ Commands ] ###################### \\
  @Rule(categories = {FEATURE, COMMAND, MOD}, options = {"true", "false", "ops", "0", "1", "2", "3", "4"})
  public static String commandCam = "false";

  // ###################### [ Validators ] ###################### \\
  private static class CarpetBotTeamValidator extends Validator<Boolean> {
    @Override
    public Boolean validate(CommandSourceStack source, CarpetRule<Boolean> changingRule, Boolean newValue, String userInput) {
      updateTeam(); return newValue;
    }
  }

  private static class CarpetBotTeamNameValidator extends Validator<String> {
    @Override
    public String validate(@Nullable CommandSourceStack source, CarpetRule<String> changingRule, String newValue, String userInput) {
      updateTeam();
      return newValue.length() <= MAX_TEAM_NAME_LENGTH ? newValue : null;
    }

    @Override
    public String description() { return "You must choose a value from 0 to 64"; }
  }

  private static class CarpetBotTeamPrefixValidator extends Validator<String> {
    @Override
    public String validate(@Nullable CommandSourceStack source, CarpetRule<String> changingRule, String newValue, String userInput) {
      updateTeam(); return newValue.length() <= MAX_TEAM_PREFIX_LENGTH ? newValue : null;
    }

    @Override
    public String description() { return "You must choose a value from 0 to 16"; }
  }

  private static class CarpetBotTeamColorValidator extends Validator<ChatFormatting> {
    @Override
    public ChatFormatting validate(@Nullable CommandSourceStack source, CarpetRule<ChatFormatting> changingRule, ChatFormatting newValue, String userInput) {
      updateTeam(); return newValue;
    }
  }
}
