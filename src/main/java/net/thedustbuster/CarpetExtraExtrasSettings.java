package net.thedustbuster;

import carpet.api.settings.CarpetRule;
import carpet.api.settings.Rule;
import carpet.api.settings.Validator;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.thedustbuster.commands.CEE_Command;
import net.thedustbuster.commands.CamCommand;
import net.thedustbuster.rules.CarpetBotTeam;
import org.jetbrains.annotations.Nullable;

import static net.thedustbuster.CarpetExtraExtrasServer.getMinecraftServer;

public class CarpetExtraExtrasSettings {
  public static final String MOD = "CarpetExtraExtras";
  public static final String FEATURE = "Feature";
  public static final String BUGFIX = "Bugfix";
  public static final String LTS = "LTS";
  public static final String COMMAND = "Command";
  public static final String VANILLA = "Vanilla";
  public static final int MAX_TEAM_NAME_LENGTH = 64;
  public static final int MAX_TEAM_PREFIX_LENGTH = 16;

  private static void updateTeam() {
    getMinecraftServer().whenDefined(server -> server.execute(CarpetBotTeam::updateTeam));
  }

  private static void updateCommand(CEE_Command instance) {
    getMinecraftServer().whenDefined(server -> server.execute(() -> instance.register(server.getCommands().getDispatcher())));
  }

  // ###################### [ Rules ] ###################### \\
  @Rule(categories = {VANILLA, MOD})
  public static boolean trackEnderPearls = false;

  @Rule(categories = {VANILLA, BUGFIX, MOD})
  public static boolean enderPearlChunkLoadingFix = false;

  @Rule(categories = {FEATURE, BUGFIX, LTS, MOD})
  public static boolean pre21ThrowableEntityBehavior = false;

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
  @Rule(categories = {FEATURE, COMMAND, MOD}, options = {"true", "false", "ops", "0", "1", "2", "3", "4"}, validators = CamCommandValidator.class)
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

  private static class CamCommandValidator extends Validator<String> {
    @Override
    public String validate(CommandSourceStack source, CarpetRule<String> changingRule, String newValue, String userInput) {
      updateCommand(CamCommand.INSTANCE); return newValue;
    }
  }
}


