package net.thedustbuster.rules;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.ShulkerBoxBlock;

public final class ContainerHelper {
  private static boolean stackHoppersToOne = true;  // Boolean to control custom stack behavior

  public static boolean isShulkerBoxChecked(ItemStack stack) {
    return !stackHoppersToOne && isShulkerBox(stack);
  }

  public static boolean isShulkerBox(ItemStack stack) {
    return stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof ShulkerBoxBlock;
  }
}
