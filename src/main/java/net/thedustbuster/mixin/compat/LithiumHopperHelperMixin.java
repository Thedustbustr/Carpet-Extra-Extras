package net.thedustbuster.mixin.compat;

import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import net.minecraft.world.level.block.entity.DropperBlockEntity;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import static net.thedustbuster.CarpetExtraExtrasSettings.*;
import static net.thedustbuster.rules.ShulkerBoxStackLimit.*;

@Pseudo
@Mixin(targets = {"net.caffeinemc.mods.lithium.common.hopper.HopperHelper"})
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
        if (isShulkerBoxAndRule(stack1) || isShulkerBox(stack2)) {
            boolean bool = switch (to) {
                case Container c when ruleEnabled(getEmptyShulkerStackLimitAllContainers()) -> stack1.getCount() < getEmptyShulkerStackLimitAllContainers();
                case HopperBlockEntity c when ruleEnabled(getEmptyShulkerStackLimitHoppers()) -> stack1.getCount() < getEmptyShulkerStackLimitHoppers();
                case DropperBlockEntity c when ruleEnabled(getEmptyShulkerStackLimitDroppers()) -> stack1.getCount() < getEmptyShulkerStackLimitDroppers();
                case DispenserBlockEntity c when ruleEnabled(getEmptyShulkerStackLimitDispensers()) -> stack1.getCount() < getEmptyShulkerStackLimitDispensers();
                default -> true;
            };

            return bool && ItemStack.isSameItemSameComponents(stack1, stack2);
        }

        return ItemStack.isSameItemSameComponents(stack1, stack2);
    }
}