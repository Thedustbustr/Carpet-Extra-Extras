package net.thedustbuster.rules;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.thedustbuster.CarpetExtraExtrasSettings;

public final class ShulkerBoxStackLimit {
  public static boolean isShulkerBoxAndRule(ItemStack stack) {
    return !ruleEnabled() && isShulkerBox(stack);
  }

  public static boolean isShulkerBox(ItemStack stack) {
    return stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof ShulkerBoxBlock;
  }

  public static boolean ruleEnabled() {
    return CarpetExtraExtrasSettings.getEmptyShulkerStackLimitAllContainers() != -1
      || CarpetExtraExtrasSettings.getEmptyShulkerStackLimitHoppers() != -1
      || CarpetExtraExtrasSettings.getEmptyShulkerStackLimitDroppers() != -1
      || CarpetExtraExtrasSettings.getEmptyShulkerStackLimitDispensers() != -1;
  }

  public static boolean ruleEnabled(int i) {
    return i != -1;
  }
}
