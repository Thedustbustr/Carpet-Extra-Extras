package net.thedustbuster.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.entity.projectile.ThrownEnderpearl;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.thedustbuster.CarpetExtraExtrasSettings;
import net.thedustbuster.rules.bpcl.PearlManager;
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
  private void appendPearl(CallbackInfo info) {
    if (!shouldProcessTick()) return;

    ServerLevel serverLevel = (ServerLevel) this.level();
    Vec3 position = new Vec3(this.getX(), this.getY(), this.getZ());
    Vec3 velocity = this.getDeltaMovement();

    PearlManager.EnderPearlData pearlData = PearlManager.updatePearl(this, position, velocity);

    if (!PearlManager.isEntityTickingChunk(serverLevel, pearlData.getNextChunkPos())) {
      info.cancel();
    }
  }

  private boolean shouldProcessTick() {
    Level level = this.level();
    Vec3 velocity = this.getDeltaMovement();

    return CarpetExtraExtrasSettings.betterEnderPearlChunkLoading && level instanceof ServerLevel && hasHorizontalMotion(velocity);
  }

  private boolean hasHorizontalMotion(Vec3 velocity) {
    return Math.abs(velocity.x) > 0.01 || Math.abs(velocity.z) > 0.01;
  }
}
