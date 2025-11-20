package net.thedustbuster.mixin.compatibility;

import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.item.ItemStack;
import net.thedustbuster.rules.ShulkerBoxStackLimit;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Pseudo
@Mixin(targets = { "net.caffeinemc.mods.lithium.common.hopper.HopperHelper" })
public class LithiumHopperHelperMixin {
  @SuppressWarnings("UnresolvedMixinReference")
  @Redirect(
    method = "Lnet/caffeinemc/mods/lithium/common/hopper/HopperHelper;tryMoveSingleItem(Lnet/minecraft/world/Container;Lnet/minecraft/world/WorldlyContainer;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;ILnet/minecraft/core/Direction;)Z",
    at = @At(
      value = "INVOKE",
      target = "Lnet/minecraft/world/item/ItemStack;isSameItemSameComponents(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)Z"
    )
  )
  private static boolean customMergeCheck(ItemStack stack1, ItemStack stack2, Container to, WorldlyContainer toSided, ItemStack transferStack, ItemStack transferChecker, int targetSlot, Direction fromDirection) {
    return ShulkerBoxStackLimit.canMergeItems(stack1, stack2, to).getOrElse(true) && ItemStack.isSameItemSameComponents(stack1, stack2);
  }
}