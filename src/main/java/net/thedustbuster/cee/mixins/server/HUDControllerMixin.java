package net.thedustbuster.cee.mixins.server;

import carpet.logging.HUDController;
import carpet.logging.LoggerRegistry;
import net.thedustbuster.cee.server.adaptors.carpet.FieldHelper;
import net.thedustbuster.cee.server.rules.CarpetBotTeam;
import net.thedustbuster.cee.server.rules.PearlTracking;
import net.thedustbuster.libs.func.Attempt;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.thedustbuster.libs.func.Unit.Unit;

@Mixin(HUDController.class)
public abstract class HUDControllerMixin {
  @Inject(method = "update_hud",
    at = @At(
      value = "INVOKE",
      target = "Ljava/util/List;forEach(Ljava/util/function/Consumer;)V",
      shift = At.Shift.AFTER
    ),
    slice = @Slice(
      from = @At(value = "INVOKE", target = "Ljava/util/List;forEach(Ljava/util/function/Consumer;)V", shift = At.Shift.BEFORE),
      to = @At(value = "INVOKE", target = "Ljava/util/List;forEach(Ljava/util/function/Consumer;)V", shift = At.Shift.AFTER, by = 2)
    ),
    remap = false
  )
  private static void cee$bots_hud(CallbackInfo ci) {
    FieldHelper.getField(LoggerRegistry.class, "cee$__bots", true, false)
      .filter(field -> Attempt.create(() -> field.getBoolean(null)).getOrHandle(e -> false))
      .whenDefined(field -> Unit(() -> LoggerRegistry.getLogger("bots").log(CarpetBotTeam::createHUD)));
  }

  @Inject(method = "update_hud", at = @At(value = "INVOKE", target = "Ljava/util/Map;keySet()Ljava/util/Set;"), remap = false)
  private static void cee$pearl_hud(CallbackInfo ci) {
    FieldHelper.getField(LoggerRegistry.class, "cee$__pearls", true, false)
      .filter(field -> Attempt.create(() -> field.getBoolean(null)).getOrHandle(e -> false))
      .whenDefined(field -> Unit(() -> LoggerRegistry.getLogger("pearls").log(PearlTracking::createHUD)));
  }
}
