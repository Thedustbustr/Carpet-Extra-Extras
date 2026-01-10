package net.thedustbuster.cee.server.rules;

import net.minecraft.world.Container;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import net.minecraft.world.level.block.entity.DropperBlockEntity;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.thedustbuster.libs.func.option.Option;

import static net.thedustbuster.cee.server.CarpetExtraExtrasSettings.*;

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
    return getStackableShulkerLimitAllContainers() != -1
      || getStackableShulkerLimitHoppers() != -1
      || getStackableShulkerLimitDroppers() != -1
      || getStackableShulkerLimitDispensers() != -1;
  }

  public static Option<Boolean> canMergeItems(ItemStack stack1, ItemStack stack2, Container destinationContainer) {
    if (isShulkerBoxAndRule(stack1) || isShulkerBox(stack2)) {
      return switch (destinationContainer) {
        case Container c when ruleEnabled(getStackableShulkerLimitAllContainers()) -> Option.of(stack1.getCount() < getStackableShulkerLimitAllContainers());
        case HopperBlockEntity c when ruleEnabled(getStackableShulkerLimitHoppers()) -> Option.of(stack1.getCount() < getStackableShulkerLimitHoppers());
        case DropperBlockEntity c when ruleEnabled(getStackableShulkerLimitDroppers()) -> Option.of(stack1.getCount() < getStackableShulkerLimitDroppers());
        case DispenserBlockEntity c when ruleEnabled(getStackableShulkerLimitDispensers()) -> Option.of(stack1.getCount() < getStackableShulkerLimitDispensers());
        default -> Option.empty();
      };
    }

    return Option.empty();
  }
}
