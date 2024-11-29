package net.thedustbuster.rules;

import net.minecraft.world.Container;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import net.minecraft.world.level.block.entity.DropperBlockEntity;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.thedustbuster.util.option.Option;

import static net.thedustbuster.CarpetExtraExtrasSettings.*;
import static net.thedustbuster.CarpetExtraExtrasSettings.getEmptyShulkerStackLimitHoppers;

public final class ShulkerBoxStackLimit {
  private static boolean isShulkerBoxAndRule(ItemStack stack) {
    return !ruleEnabled() && isShulkerBox(stack);
  }

  private static boolean isShulkerBox(ItemStack stack) {
    return stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof ShulkerBoxBlock;
  }

  private static boolean ruleEnabled(int i) {
    return i != -1;
  }

  private static boolean ruleEnabled() {
    return getEmptyShulkerStackLimitAllContainers() != -1
            || getEmptyShulkerStackLimitHoppers() != -1
            || getEmptyShulkerStackLimitDroppers() != -1
            || getEmptyShulkerStackLimitDispensers() != -1;
  }

  public static Option<Boolean> canMergeItems(ItemStack stack1, ItemStack stack2, Container destinationContainer) {
    if (isShulkerBoxAndRule(stack1) || isShulkerBox(stack2)) {
      return switch (destinationContainer) {
        case Container c when ruleEnabled(getEmptyShulkerStackLimitAllContainers()) -> Option.of(stack1.getCount() < getEmptyShulkerStackLimitAllContainers());
        case HopperBlockEntity c when ruleEnabled(getEmptyShulkerStackLimitHoppers()) -> Option.of(stack1.getCount() < getEmptyShulkerStackLimitHoppers());
        case DropperBlockEntity c when ruleEnabled(getEmptyShulkerStackLimitDroppers()) -> Option.of(stack1.getCount() < getEmptyShulkerStackLimitDroppers());
        case DispenserBlockEntity c when ruleEnabled(getEmptyShulkerStackLimitDispensers()) -> Option.of(stack1.getCount() < getEmptyShulkerStackLimitDispensers());
        default -> Option.empty();
      };
    }

    return Option.empty();
  }
}
