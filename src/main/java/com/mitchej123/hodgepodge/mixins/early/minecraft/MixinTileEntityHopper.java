package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.IHopper;
import net.minecraft.tileentity.TileEntityHopper;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(TileEntityHopper.class)
public class MixinTileEntityHopper {

    /**
     * @author MuXiu1997
     * @reason Full hopper voiding items from drawer
     */
    @Redirect(
            method = "func_145891_a(Lnet/minecraft/tileentity/IHopper;)Z",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/tileentity/TileEntityHopper;func_145892_a(Lnet/minecraft/tileentity/IHopper;Lnet/minecraft/inventory/IInventory;II)Z"))
    private static boolean hodgepodge$moveFromHopperToInventory(IHopper hopper, IInventory inventory, int slot,
            int side) {
        ItemStack contentSlot = inventory.getStackInSlot(slot);

        if (contentSlot == null || contentSlot.stackSize == 0) return false;

        if (inventory instanceof ISidedInventory) {
            ISidedInventory sidedInventory = (ISidedInventory) inventory;
            if (!sidedInventory.canExtractItem(slot, contentSlot, side)) return false;
        }

        int spaceSlot = getSpaceSlot(hopper, contentSlot);
        if (spaceSlot == -1) return false;

        ItemStack decreased = inventory.decrStackSize(slot, 1);
        if (decreased == null || decreased.stackSize == 0) return false;

        ItemStack space = hopper.getStackInSlot(spaceSlot);
        if (space == null) {
            space = contentSlot.copy();
            space.stackSize = 1;
            hopper.setInventorySlotContents(spaceSlot, space);
        } else {
            space.stackSize += 1;
        }

        inventory.markDirty();
        return true;
    }

    @Unique
    private static int getSpaceSlot(IHopper hopper, ItemStack is) {
        final int size = hopper.getSizeInventory();
        for (int i = 0; i < size; i++) {
            ItemStack space = hopper.getStackInSlot(i);
            if (space == null) {
                return i;
            }
            if (space.stackSize < Math.min(space.getMaxStackSize(), hopper.getInventoryStackLimit()))
                if (is.isItemEqual(space) && ItemStack.areItemStackTagsEqual(is, space)) {
                    return i;
                }
        }
        return -1;
    }
}
