package net.thedustbuster.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.thedustbuster.CarpetExtraExtrasSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ThrowableProjectile.class)
public abstract class ThrowableProjectileMixin extends Entity implements ProjectileAccessor {
  public ThrowableProjectileMixin(EntityType<?> entityType, Level level) {
    super(entityType, level);
  }

  @Shadow
  public abstract void applyInertia();

  @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
  private void tick(CallbackInfo info) {
    if (!CarpetExtraExtrasSettings.pre21ThrowableEntityBehaviorReintroduced) return;

    HitResult hitResult = ProjectileUtil.getHitResultOnMoveVector(this, this::invokeCanHitEntity);
    Vec3 newPosition = calculateNewPosition(hitResult);

    this.setPos(newPosition);
    this.invokeUpdateRotation();
    this.applyEffectsFromBlocks();

    super.tick();

    if (shouldHandleHitResult(hitResult)) {
      this.invokeHitTargetOrDeflectSelf(hitResult);
    }

    this.applyInertia();
    this.applyGravity();

    info.cancel(); // Cancel the rest of the original tick method
  }

  private Vec3 calculateNewPosition(HitResult hitResult) {
    return (hitResult.getType() != HitResult.Type.MISS)
      ? hitResult.getLocation()
      : this.position().add(this.getDeltaMovement());
  }

  private boolean shouldHandleHitResult(HitResult hitResult) {
    return hitResult.getType() != HitResult.Type.MISS && this.isAlive();
  }
}
