package net.thedustbuster.cee.mixins.server;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.thedustbuster.cee.server.CarpetExtraExtrasSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ThrowableProjectile.class)
/* Projectile cannot be extended because its constructor is package-private. So instead we extend the ProjectileMixin that exposes the instance's tick() method
   and the ProjectileAccessor interface that exposes any other required protected methods */
public abstract class ThrowableProjectileMixin extends ProjectileMixin implements ProjectileAccessor {
  public ThrowableProjectileMixin(EntityType<?> entityType, Level level) {
    super(entityType, level);
  }

  @Shadow
  protected abstract void applyInertia();

  @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
  public void cee$tick(CallbackInfo info) {
    if (!CarpetExtraExtrasSettings.pre21ThrowableEntityBehavior) return;

    HitResult hitResult = ProjectileUtil.getHitResultOnMoveVector(this, this::invokeCanHitEntity);
    Vec3 newPosition = cee$calculateNewPosition(hitResult);

    this.setPos(newPosition);
    this.invokeUpdateRotation();
    this.applyEffectsFromBlocks();

    super.tick();

    if (cee$shouldHandleHitResult(hitResult)) {
      this.invokeHitTargetOrDeflectSelf(hitResult);
    }

    this.applyInertia();
    this.applyGravity();

    info.cancel(); // Cancel the rest of the original tick method
  }

  @Unique
  private Vec3 cee$calculateNewPosition(HitResult hitResult) {
    return (hitResult.getType() != HitResult.Type.MISS)
      ? hitResult.getLocation()
      : this.position().add(this.getDeltaMovement());
  }

  @Unique
  private boolean cee$shouldHandleHitResult(HitResult hitResult) {
    return hitResult.getType() != HitResult.Type.MISS && this.isAlive();
  }
}
