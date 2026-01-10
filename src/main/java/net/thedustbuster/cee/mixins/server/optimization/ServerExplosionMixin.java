package net.thedustbuster.cee.mixins.server.optimization;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ServerExplosion;
import net.thedustbuster.cee.server.CarpetExtraExtrasSettings;
import net.thedustbuster.libs.func.For;
import net.thedustbuster.cee.server.tags.LazyTntTag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import static net.thedustbuster.libs.func.collection.HList.$1;
import static net.thedustbuster.cee.server.util.libs.testing.Option.asType;

@Mixin(ServerExplosion.class)
public abstract class ServerExplosionMixin implements Explosion, LazyTntTag {
  @Redirect(
    method = "hurtEntities",
    at = @At(
      value = "INVOKE",
      target = "Lnet/minecraft/world/entity/Entity;ignoreExplosion(Lnet/minecraft/world/level/Explosion;)Z"
    )
  )
  /* Because the original if statement is negating, the redirected code here will also get negated, hence the inverse logic. */
  public boolean cee$ignoreExplosion(Entity entity, Explosion explosion) {
    if (!CarpetExtraExtrasSettings.optimizedTNTInteraction) return entity.ignoreExplosion(this);
    return For
      .start(asType(entity.level(), ServerLevel.class))
      .flatMap(__ -> asType(entity, PrimedTnt.class))
      .flatMap(tnt -> asType(tnt, LazyTntTag.class))
      .yield((ltnt, hlist) ->
        $1(hlist).getFuse() <= 1 && !ltnt.cee$spawnedInLazyChunk()
      )
      .getOrElse(entity.ignoreExplosion(this));
  }
}