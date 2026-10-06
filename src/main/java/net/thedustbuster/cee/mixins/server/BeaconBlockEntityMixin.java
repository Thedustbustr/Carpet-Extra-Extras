package net.thedustbuster.cee.mixins.server;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.thedustbuster.cee.server.CarpetExtraExtrasSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BeaconBlockEntity.class)
public abstract class BeaconBlockEntityMixin {
  @WrapOperation(
    method = "tick",
    at = @At(
      value = "INVOKE",
      target = "Lnet/minecraft/world/level/block/state/BlockState;getLightDampening()I"
    )
  )
  private static int cee$tintedGlassPassesBeam(BlockState state, Operation<Integer> original, @Local(argsOnly = true, name = "level") Level level) {
    if (CarpetExtraExtrasSettings.tintedGlassHidesBeaconBeam && !level.isClientSide() && state.is(Blocks.TINTED_GLASS)) return 0;
    return original.call(state);
  }
}