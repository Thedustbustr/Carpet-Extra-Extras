package net.thedustbuster.mixin.optimization;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ServerExplosion;
import net.thedustbuster.CarpetExtraExtrasSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerExplosion.class)
public abstract class ServerExplosionMixin implements Explosion {
  @Redirect(
    method = "hurtEntities",
    at = @At(
      value = "INVOKE",
      target = "Lnet/minecraft/world/entity/Entity;ignoreExplosion(Lnet/minecraft/world/level/Explosion;)Z"
    )
  )
  public boolean customEntityCheck(Entity entity, Explosion explosion) {
    // Because the original if statement is negating, the redirected code here will also get negated, hence the inverse logic.
    if (CarpetExtraExtrasSettings.optimizedTNTInteraction) {
      return entity.ignoreExplosion(this) || (entity instanceof PrimedTnt && ((PrimedTnt) entity).getFuse() == 1);
    }

    return entity.ignoreExplosion(this);
  }
}