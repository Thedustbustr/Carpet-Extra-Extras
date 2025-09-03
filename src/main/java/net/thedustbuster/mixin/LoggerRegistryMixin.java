package net.thedustbuster.mixin;

import carpet.logging.LoggerRegistry;
import net.thedustbuster.adaptors.carpet.LoggerHelper;
import net.thedustbuster.util.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.thedustbuster.util.func.Unit.Unit;

@Mixin(LoggerRegistry.class)
public abstract class LoggerRegistryMixin {
  @Unique
  private static boolean __bots;

  @Unique
  private static boolean __pearls;

  @Inject(method = "registerLoggers", at = @At("TAIL"), remap = false)
  private static void registerLoggers(CallbackInfo cb) {
    LoggerHelper.createHUDLogger("bots")
      .fold(
        logger -> {
          LoggerRegistry.registerLogger("bots", logger);
          return Unit;
        },
        () -> Logger.warn("Could not create HUDLogger for bots! This feature will not be present!")
      );

    LoggerHelper.createHUDLogger("pearls")
      .fold(
        logger -> Unit(() -> LoggerRegistry.registerLogger("pearls", logger)),
        () -> Logger.warn("Could not create HUDLogger for pearls! This feature will not be present!")
      );
  }
}