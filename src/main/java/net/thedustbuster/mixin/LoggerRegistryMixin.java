package net.thedustbuster.mixin;

import carpet.logging.HUDLogger;
import carpet.logging.LoggerRegistry;
import net.thedustbuster.BetterCarpetBots;
import net.thedustbuster.adaptors.carpet.LoggerHelper;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LoggerRegistry.class)
public abstract class LoggerRegistryMixin {
  private static boolean __bots;

  @Inject(method = "registerLoggers", at = @At("TAIL"), remap = false)
  private static void registerLoggers(CallbackInfo cb) {
    @Nullable HUDLogger logger = LoggerHelper.createHUDLogger("bots");

    if (logger == null) {
      BetterCarpetBots.LOGGER.warn("Could not create HUDLogger for bots! This feature will not be present!");
    } else { LoggerRegistry.registerLogger("bots", logger); }
  }
}