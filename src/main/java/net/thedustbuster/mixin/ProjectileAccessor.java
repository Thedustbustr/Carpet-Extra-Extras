package net.thedustbuster.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileDeflection;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

// Used to access required protected methods in Projectile
@Mixin(Projectile.class)
public interface ProjectileAccessor {
  @Invoker("canHitEntity")
  boolean invokeCanHitEntity(Entity entity);

  @Invoker("updateRotation")
  void invokeUpdateRotation();

  @Invoker("hitTargetOrDeflectSelf")
  ProjectileDeflection invokeHitTargetOrDeflectSelf(HitResult hitResult);
}