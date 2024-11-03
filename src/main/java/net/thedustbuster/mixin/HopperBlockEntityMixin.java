package net.thedustbuster.mixin;

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.thedustbuster.rules.ContainerHelper.isShulkerBox;
import static net.thedustbuster.rules.ContainerHelper.isShulkerBoxChecked;

@Mixin(HopperBlockEntity.class)
public abstract class HopperBlockEntityMixin {

  @Redirect(method = "tryMoveInItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/HopperBlockEntity;canMergeItems(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)Z"))
  private static boolean injectTryMoveInItem(ItemStack first, ItemStack second, Container container, Container container2) {
    if (isShulkerBoxChecked(first) || isShulkerBox(second)) {
      if (container2 instanceof HopperBlockEntity && first.getCount() >= 1) {
        return false;
      }
    }

    return canMergeItems(first, second);
  }


  @Unique
  private static boolean canMergeItems(ItemStack itemStack, ItemStack itemStack2) {
    return itemStack.getCount() <= itemStack.getMaxStackSize() && ItemStack.isSameItemSameComponents(itemStack, itemStack2);
  }
}