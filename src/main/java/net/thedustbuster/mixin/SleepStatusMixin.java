package net.thedustbuster.mixin;

import net.minecraft.server.players.SleepStatus;
import net.thedustbuster.CarpetExtraExtrasSettings;
import net.thedustbuster.rules.bots.CarpetBotRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SleepStatus.class)
public abstract class SleepStatusMixin {
  @Shadow
  private int activePlayers;

  @Inject(method = "update", at = @At("TAIL"))
  private void update(CallbackInfoReturnable cb) {
    if (CarpetExtraExtrasSettings.carpetBotsSkipNight) {
      this.activePlayers = this.activePlayers - CarpetBotRules.getBots();
    }
  }
}