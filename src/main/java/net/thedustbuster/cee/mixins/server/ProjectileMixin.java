package net.thedustbuster.cee.mixins.server;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Projectile.class)
public abstract class ProjectileMixin extends Entity {
  public ProjectileMixin(EntityType<?> entityType, Level level) {
    super(entityType, level);
  }

  @Inject(method = "tick", at = @At("HEAD"))
  public void tick(CallbackInfo info) {
    // Just used to call tick in Projectile
  }
}