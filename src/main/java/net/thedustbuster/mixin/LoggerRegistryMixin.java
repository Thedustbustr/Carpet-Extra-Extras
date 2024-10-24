package net.thedustbuster.mixin;

import carpet.logging.LoggerRegistry;
import net.thedustbuster.CarpetExtraExtrasServer;
import net.thedustbuster.adaptors.carpet.LoggerHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.thedustbuster.util.Unit.Unit;

@Mixin(LoggerRegistry.class)
public abstract class LoggerRegistryMixin {
  private static boolean __bots;
  private static boolean __pearls;

  @Inject(method = "registerLoggers", at = @At("TAIL"), remap = false)
  private static void registerLoggers(CallbackInfo cb) {
    LoggerHelper.createHUDLogger("bots")
            .fold(
              logger -> {
                LoggerRegistry.registerLogger("bots", logger);
                return Unit;
              },
              () -> {
                CarpetExtraExtrasServer.LOGGER.warn("Could not create HUDLogger for bots! This feature will not be present!");
                return Unit;
              }
            );

    LoggerHelper.createHUDLogger("pearls")
            .fold(
              logger -> {
                LoggerRegistry.registerLogger("pearls", logger);
                return Unit;
              },
              () -> {
                CarpetExtraExtrasServer.LOGGER.warn("Could not create HUDLogger for pearls! This feature will not be present!");
                return Unit;
              }
            );
  }
}