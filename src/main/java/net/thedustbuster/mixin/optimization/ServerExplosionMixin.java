package net.thedustbuster.mixin.optimization;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ServerExplosion;
import net.thedustbuster.CarpetExtraExtrasSettings;
import net.thedustbuster.adaptors.minecraft.worldgen.ChunkHelper;
import net.thedustbuster.adaptors.minecraft.worldgen.ChunkLevel;
import net.thedustbuster.libs.func.For;
import net.thedustbuster.tags.LazyTntTag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import static net.thedustbuster.libs.func.collection.HList.$3;
import static net.thedustbuster.libs.func.option.None.None;
import static net.thedustbuster.libs.func.option.Some.Some;
import static net.thedustbuster.util.libs.testing.Option.asType;

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
    if (!CarpetExtraExtrasSettings.optimizedTNTInteraction || !(entity.level() instanceof ServerLevel)) return entity.ignoreExplosion(this);
    return For
      .start(entity)
      .flatMap(e -> asType(e, PrimedTnt.class))
      .flatMap(tnt -> (tnt.getFuse() <= 1) ? Some(tnt) : None())
      .flatMap(tnt -> asType(tnt, LazyTntTag.class))
      .flatMap(ltnt -> ltnt.cee$spawnedInLazyChunk() ? None() : Some(ltnt))
      .yield((__, hlist) -> {
        PrimedTnt tnt = $3(hlist);
        int cx = tnt.chunkPosition().x;
        int cz = tnt.chunkPosition().z;

        // If the entity is chunk is not lazy, apply optimization (inverse logic)
        return !ChunkHelper.isChunkAtLevel((ServerLevel) tnt.level(), ChunkLevel.LAZY, cx, cz);
      }).getOrElse(entity.ignoreExplosion(this));
  }
}