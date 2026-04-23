package net.thedustbuster.cee.server.commands;

import carpet.utils.CommandHelper;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.protocol.common.ClientboundTransferPacket;
import net.minecraft.server.level.ServerPlayer;
import net.thedustbuster.cee.server.CarpetExtraExtrasServer;
import net.thedustbuster.cee.server.CarpetExtraExtrasSettings;
import net.thedustbuster.cee.server.adaptors.minecraft.MessagingHelper;
import net.thedustbuster.cee.server.adaptors.minecraft.text.TextBuffer;
import net.thedustbuster.libs.core.classloading.LoadAtRuntime;
import net.thedustbuster.libs.core.tuple.Triple;
import net.thedustbuster.libs.func.Attempt;
import net.thedustbuster.libs.func.Either;
import net.thedustbuster.libs.func.option.Option;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;
import static net.thedustbuster.cee.server.adaptors.minecraft.text.TextBuffer.text;
import static net.thedustbuster.libs.func.Either.Left;
import static net.thedustbuster.libs.func.Either.Right;


@LoadAtRuntime
public final class WarpCommand implements CEE_Command {
  public static final WarpCommand INSTANCE = new WarpCommand();
  private WarpCommand() { }

  static {
    CarpetExtraExtrasServer.registerCommand(INSTANCE);
  }

  @Override
  public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
    dispatcher.register(literal("warp")
      .requires(player -> CommandHelper.canUseCommand(player, CarpetExtraExtrasSettings.commandWarp))
      .then(argument("destination", StringArgumentType.word())
        .suggests(this::suggestDestinations)
        .executes(c -> executeCommand(c.getSource(), StringArgumentType.getString(c, "destination")))
      )
    );
  }

  private int executeCommand(CommandSourceStack source, String alias) {
    return Attempt.create(() -> {
      ServerPlayer player = source.getPlayerOrException();
      return parseWarpDestinations().fold(
        err -> {
          source.sendFailure(text(err));
          return 0;
        },
        destinations -> destinations.stream()
          .filter(d -> d.match((a, _, _) -> a.equals(alias))).findFirst()
          .map(d -> d.match((_, hostname, port) -> {
            MessagingHelper.sendActionBarMessage(player,
              new TextBuffer()
                .addText("Warping -> ", ChatFormatting.DARK_AQUA)
                .addText(hostname, ChatFormatting.WHITE)
                .build()
            );
            
            player.connection.send(new ClientboundTransferPacket(hostname, port.getOrElse(25565)));
            return 1;
          })).orElse(0)
      );
    }).getOrHandle(_ -> 0);
  }

  private CompletableFuture<Suggestions> suggestDestinations(CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) {
    parseWarpDestinations().fold(
      _ -> new ArrayList<String>(),
      destinations -> destinations.stream().map(d -> d.match((alias, _, _) -> alias)).toList()
    ).forEach(builder::suggest);
    return builder.buildFuture();
  }

  public static Either<String, List<Triple<String, String, Option<Integer>>>> parseWarpDestinations() { return parseWarpDestinations(CarpetExtraExtrasSettings.warpDestinations); }
  public static Either<String, List<Triple<String, String, Option<Integer>>>> parseWarpDestinations(String destinationsStr) {
    if (destinationsStr.equals("none")) return Right(List.of());
    List<Triple<String, String, Option<Integer>>> destinations = new ArrayList<>();
    for (String part : destinationsStr.split(",")) {
      if (part.isBlank()) continue;
      var parsed = parseWarpDestination(part);
      if (parsed.isLeft()) return Left(parsed.left().get());
      destinations.add(parsed.right().get());
    }
    return Right(destinations);
  }

  private static final Pattern DESTINATION_PATTERN = Pattern.compile("^([^=]+)=([^:]+)(?::(\\d+))?$");
  private static Either<String, Triple<String, String, Option<Integer>>> parseWarpDestination(String s) {
    Matcher m = DESTINATION_PATTERN.matcher(s.trim());
    if (!m.matches()) return Left(String.format("Invalid destination '%s'", s));

    String alias = m.group(1).trim();
    String host = m.group(2).trim();
    Option<Integer> port = Option.of(m.group(3))
      .flatMap(pstr -> Attempt.create(() -> Integer.parseInt(pstr)).toOption());

    return Right(new Triple<>(alias, host, port));
  }
}