package net.thedustbuster.cee.mixins.server.optimization;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.level.Level;
import net.thedustbuster.cee.server.CarpetExtraExtrasSettings;
import net.thedustbuster.cee.server.adaptors.minecraft.worldgen.ChunkHelper;
import net.thedustbuster.cee.server.tags.LazyTntTag;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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
  private void cee$detectLazyChunk(Level level, double x, double y, double z, @Nullable LivingEntity livingEntity, CallbackInfo ci) {
    if (CarpetExtraExtrasSettings.optimizedTNTInteraction && level instanceof ServerLevel lvl) {
      // Convert to chunk coords
      int cx = ((int) x) >> 4;
      int cz = ((int) z) >> 4;

      this.cee$spawnedInLazyChunk = ChunkHelper.isLazy(lvl, cx, cz);
    }
  }
}