package net.thedustbuster.mixin;

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.thedustbuster.rules.ShulkerBoxStackLimit;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(HopperBlockEntity.class)
public abstract class HopperBlockEntityMixin {

  @Redirect(
    method = "tryMoveInItem",
    at = @At(
      value = "INVOKE",
      target = "Lnet/minecraft/world/level/block/entity/HopperBlockEntity;canMergeItems(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)Z"
    )
  )
  private static boolean injectTryMoveInItem(ItemStack stack1, ItemStack stack2, Container container, Container container2) {
    return ShulkerBoxStackLimit.canMergeItems(stack1, stack2, container2).getOrElse(canMergeItems(stack1, stack2));
  }

  @Shadow
  private static boolean canMergeItems(ItemStack itemStack, ItemStack itemStack2) {
    throw new IllegalStateException(); // *Should* never get here
  }
}