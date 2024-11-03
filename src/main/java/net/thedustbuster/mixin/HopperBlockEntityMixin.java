package net.thedustbuster.mixin;

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import net.minecraft.world.level.block.entity.DropperBlockEntity;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import static net.thedustbuster.CarpetExtraExtrasSettings.*;
import static net.thedustbuster.rules.ShulkerBoxStackLimit.*;

@Mixin(HopperBlockEntity.class)
public abstract class HopperBlockEntityMixin {

  @Redirect(method = "tryMoveInItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/HopperBlockEntity;canMergeItems(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)Z"))
  private static boolean injectTryMoveInItem(ItemStack first, ItemStack second, Container container, Container container2) {
    if (isShulkerBoxAndRule(first) || isShulkerBox(second)) {
      return switch (container2) {
        case Container c when ruleEnabled(getEmptyShulkerStackLimitAllContainers()) -> first.getCount() < getEmptyShulkerStackLimitAllContainers();
        case HopperBlockEntity c when ruleEnabled(getEmptyShulkerStackLimitHoppers()) -> first.getCount() < getEmptyShulkerStackLimitHoppers();
        case DropperBlockEntity c when ruleEnabled(getEmptyShulkerStackLimitDroppers()) -> first.getCount() < getEmptyShulkerStackLimitDroppers();
        case DispenserBlockEntity c when ruleEnabled(getEmptyShulkerStackLimitDispensers()) -> first.getCount() < getEmptyShulkerStackLimitDispensers();
        default -> canMergeItems(first, second);
      };
    }

    return canMergeItems(first, second);
  }

  @Shadow
  private static boolean canMergeItems(ItemStack itemStack, ItemStack itemStack2) {
    throw new IllegalStateException(); // *Should* never get here
  }
}