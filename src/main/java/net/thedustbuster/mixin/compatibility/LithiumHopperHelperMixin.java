package net.thedustbuster.mixin.compatibility;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.Container;
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
    method = "tryMoveSingleItem(Lnet/minecraft/world/Container;Lnet/minecraft/world/WorldlyContainer;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;ILnet/minecraft/core/Direction;)Z",
    at = @At(
      value = "INVOKE",
      target = "Lnet/minecraft/world/item/ItemStack;isSameItemSameComponents(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)Z"
    )
  )
  private static boolean customMergeCheck(ItemStack stack1, ItemStack stack2, @Local(argsOnly = true) Container container) {
    return ShulkerBoxStackLimit.canMergeItems(stack1, stack2, container).getOrElse(true) && ItemStack.isSameItemSameComponents(stack1, stack2);
  }
}