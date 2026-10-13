package net.thedustbuster.cee.mixins.server;

import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.piston.PistonMovingBlockEntity;
import net.thedustbuster.cee.server.CarpetExtraExtrasSettings;
import net.thedustbuster.cee.server.rules.PistonPushDesyncFix;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PistonMovingBlockEntity.class)
public abstract class PistonMovingBlockEntityMixin {
  @Inject(method = "moveEntityByPiston", at = @At("TAIL"))
  private static void cee$recordPlayerPushEvent(Direction pistonDirection, Entity entity, double delta, Direction movement, CallbackInfo ci) {
    if (CarpetExtraExtrasSettings.pistonPushDesyncFix && entity instanceof ServerPlayer player) {
      PistonPushDesyncFix.recordPushEvent(player, movement, delta);
    }
  }
}
