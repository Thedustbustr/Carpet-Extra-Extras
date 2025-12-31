package net.thedustbuster.mixin.optimization;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.level.Level;
import net.thedustbuster.adaptors.minecraft.worldgen.ChunkHelper;
import net.thedustbuster.adaptors.minecraft.worldgen.ChunkLevel;
import net.thedustbuster.tags.LazyTntTag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//import net.minecraft.world.level.chunk.ChunkAccess

@Mixin(PrimedTnt.class)
public abstract class PrimedTntMixin implements LazyTntTag {
  @Unique
  private boolean cee$spawnedInLazyChunk;

  @Override
  public boolean cee$spawnedInLazyChunk() {
    return this.cee$spawnedInLazyChunk;
  }

  @Inject(
    method = "<init>(Lnet/minecraft/world/level/Level;DDDLnet/minecraft/world/entity/LivingEntity;)V",
    at = @At("TAIL")
  )
  private void cee$detectLazyChunk(Level level, double x, double y, double z, LivingEntity livingEntity, CallbackInfo ci) {
    if (level instanceof ServerLevel lvl) {
      // Convert to chunk coords
      int cx = ((int) x) >> 4;
      int cz = ((int) z) >> 4;

      this.cee$spawnedInLazyChunk = ChunkHelper.isChunkAtLevel(lvl, ChunkLevel.LAZY, cx, cz);
    }
  }
}