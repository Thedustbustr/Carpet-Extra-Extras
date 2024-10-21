package net.thedustbuster.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.entity.projectile.ThrownEnderpearl;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.thedustbuster.CarpetExtraExtrasSettings;
import net.thedustbuster.rules.PearlTracking;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ThrownEnderpearl.class)
public abstract class ThrownEnderpearlMixin extends ThrowableItemProjectile {

  protected ThrownEnderpearlMixin(EntityType<? extends ThrowableItemProjectile> entityType, Level level) {
    super(entityType, level);
  }

  @Inject(method = "tick()V", at = @At(value = "HEAD"), cancellable = true)
  private void onTick(CallbackInfo info) {
    if (!shouldProcessTick()) return;

    Vec3 position = new Vec3(this.getX(), this.getY(), this.getZ());
    Vec3 velocity = this.getDeltaMovement();

    PearlTracking.updatePearl(this, position, velocity);
  }

  private boolean shouldProcessTick() {
    Level level = this.level();

    return CarpetExtraExtrasSettings.trackEnderPearls && level instanceof ServerLevel;
  }
}
