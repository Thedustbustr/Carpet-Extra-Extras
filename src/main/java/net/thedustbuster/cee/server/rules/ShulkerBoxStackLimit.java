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
  private static final int DISABLED = -1;

  public static Option<Boolean> canStackShulker(ItemStack stack1, ItemStack stack2, Container destinationContainer) {
    int limit = limitOf(destinationContainer);
    if (limit == DISABLED || !isShulkerBox(stack2)) return Option.empty();
    return Option.of(stack1.getCount() < limit);
  }

  private static boolean isShulkerBox(ItemStack stack) {
    return stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof ShulkerBoxBlock;
  }

  private static int limitOf(Container destinationContainer) {
    int allContainers = getStackableShulkerLimitAllContainers();
    if (allContainers != DISABLED) return allContainers;

    return switch (destinationContainer) {
      case HopperBlockEntity _ -> getStackableShulkerLimitHoppers();
      case DropperBlockEntity _ -> getStackableShulkerLimitDroppers();
      case DispenserBlockEntity _ -> getStackableShulkerLimitDispensers();
      default -> DISABLED;
    };
  }
}