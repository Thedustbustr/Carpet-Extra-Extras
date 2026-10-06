package net.thedustbuster.cee.mixins.server;

import carpet.patches.EntityPlayerMPFake;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.SleepStatus;
import net.thedustbuster.cee.server.CarpetExtraExtrasSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(SleepStatus.class)
public abstract class SleepStatusMixin {
  @Shadow
  private int activePlayers;

  @Inject(method = "update", at = @At("TAIL"))
  private void cee$update(List<ServerPlayer> list, CallbackInfoReturnable<Boolean> infoReturnable) {
    if (CarpetExtraExtrasSettings.carpetBotsSkipNight) {
      // Each level has its own SleepStatus, so only count the bots it was just updated with
      this.activePlayers -= (int) list.stream().filter(p -> p instanceof EntityPlayerMPFake && !p.isSpectator()).count();
    }
  }
}