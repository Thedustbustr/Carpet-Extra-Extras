package net.thedustbuster.mixin;

import carpet.logging.HUDLogger;
import carpet.logging.LoggerRegistry;
import net.thedustbuster.CarpetExtraExtrasServer;
import net.thedustbuster.adaptors.carpet.LoggerHelper;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LoggerRegistry.class)
public abstract class LoggerRegistryMixin {
  private static boolean __bots;
  private static boolean __pearls;

  @Inject(method = "registerLoggers", at = @At("TAIL"), remap = false)
  private static void registerLoggers(CallbackInfo cb) {
    @Nullable HUDLogger bot_logger = LoggerHelper.createHUDLogger("bots");
    @Nullable HUDLogger pearl_logger = LoggerHelper.createHUDLogger("pearls");

    if (bot_logger == null) {
      CarpetExtraExtrasServer.LOGGER.warn("Could not create HUDLogger for bots! This feature will not be present!");
    } else { LoggerRegistry.registerLogger("bots", bot_logger); }

    if (pearl_logger == null) {
      CarpetExtraExtrasServer.LOGGER.warn("Could not create HUDLogger for pearls! This feature will not be present!");
    } else { LoggerRegistry.registerLogger("pearls", pearl_logger); }
  }
}