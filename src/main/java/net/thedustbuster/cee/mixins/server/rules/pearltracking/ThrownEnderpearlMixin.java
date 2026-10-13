package net.thedustbuster.cee.mixins.server.rules.pearltracking;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrowableItemProjectile;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrownEnderpearl;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.thedustbuster.cee.server.CarpetExtraExtrasSettings;
import net.thedustbuster.cee.server.rules.PearlTracking;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// Applied before the pre21throwables mixin (default priority 1000), which cancels tick at HEAD
@Mixin(value = ThrownEnderpearl.class, priority = 900)
public abstract class ThrownEnderpearlMixin extends ThrowableItemProjectile {
  @Unique
  private ThrownEnderpearl self() {
    return (ThrownEnderpearl) (Object) this;
  }

  protected ThrownEnderpearlMixin(EntityType<? extends ThrowableItemProjectile> entityType, Level level) {
    super(entityType, level);
  }

  @Inject(method = "tick", at = @At(value = "HEAD"))
  private void cee$trackPearl(CallbackInfo info) {
    if (!(this.level() instanceof ServerLevel) || !CarpetExtraExtrasSettings.trackEnderPearls) return;

    Vec3 position = new Vec3(this.getX(), this.getY(), this.getZ());
    Vec3 velocity = this.getDeltaMovement();

    PearlTracking.updatePearl(self(), position, velocity);
  }

  @Inject(method = "onRemoval", at = @At(value = "HEAD"))
  private void onRemoval(RemovalReason removalReason, CallbackInfo info) {
    if (!(this.level() instanceof ServerLevel)) return;
    PearlTracking.removePearl(this.getUUID());
  }
}
