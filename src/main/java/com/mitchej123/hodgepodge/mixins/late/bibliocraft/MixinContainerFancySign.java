package com.mitchej123.hodgepodge.mixins.late.bibliocraft;

import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import jds.bibliocraft.blocks.ContainerFancySign;

@Mixin(ContainerFancySign.class)
public abstract class MixinContainerFancySign extends Container {

    @WrapOperation(
            method = "transferStackInSlot",
            at = @At(
                    value = "INVOKE",
                    target = "Ljds/bibliocraft/blocks/ContainerFancySign;mergeItemStack(Lnet/minecraft/item/ItemStack;IIZ)Z"))
    private boolean hodgepodge$respectFancySignSlotLimits(ContainerFancySign instance, ItemStack stack, int start,
            int end, boolean reverse, Operation<Boolean> original) {
        if (start == 0 && end == 2) {

            boolean changed = false;
            for (int i = 0; i < 2 && stack.stackSize > 0; i++) {
                Slot slot = inventorySlots.get(i);
                if (!slot.getHasStack() && slot.isItemValid(stack)) {
                    slot.putStack(stack.splitStack(Math.min(stack.stackSize, slot.getSlotStackLimit())));
                    slot.onSlotChanged();
                    changed = true;
                }
            }
            return changed;
        }

        return original.call(instance, stack, start, end, reverse);
    }
}
