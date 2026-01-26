package net.thedustbuster.cee.mixins.server;

import carpet.patches.EntityPlayerMPFake;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.scores.PlayerTeam;
import net.thedustbuster.cee.server.CarpetExtraExtrasSettings;
import net.thedustbuster.cee.server.rules.CarpetBotTeam;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Player.class)
public abstract class PlayerMixin {
  @Redirect(
    method = "getDisplayName",
    at = @At(
      value = "INVOKE",
      target = "Lnet/minecraft/world/entity/player/Player;getTeam()Lnet/minecraft/world/scores/PlayerTeam;"
    )
  )
  private PlayerTeam cee$fixJoinMsg(Player p) {
    if (CarpetExtraExtrasSettings.carpetBotTeam && p instanceof EntityPlayerMPFake) return CarpetBotTeam.getTeam();
    return p.getTeam();
  }
}
