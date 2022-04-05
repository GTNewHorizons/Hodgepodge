package com.mitchej123.hodgepodge.mixins.minecraft;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.IHopper;
import net.minecraft.tileentity.TileEntityHopper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(TileEntityHopper.class)
public class MixinTileEntityHopper {
    /**
     * @author MuXiu1997
     * @reason Full hopper voiding items from drawer
     */
    @Redirect(method = "func_145891_a(Lnet/minecraft/tileentity/IHopper;)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/tileentity/TileEntityHopper;func_145892_a(Lnet/minecraft/tileentity/IHopper;Lnet/minecraft/inventory/IInventory;II)Z"))
    private static boolean redirect_func_145891_a(IHopper hopper, IInventory inventory, int slot, int side) {
        ItemStack is = inventory.getStackInSlot(slot);
        if (is != null && is.stackSize != 0) {
            int spaceSlot = getSpaceSlot(hopper, is);
            if (spaceSlot != -1) {
                ItemStack decreased = inventory.decrStackSize(slot, 1);
                if (decreased != null && decreased.stackSize != 0) {
                    ItemStack space = hopper.getStackInSlot(spaceSlot);
                    if (space == null) {
                        hopper.setInventorySlotContents(spaceSlot, oneSizedCopy(is));
                    } else {
                        space.stackSize += 1;
                    }
                    return true;
                }
            }
        }
        return false;
    }

    private static int getSpaceSlot(IHopper hopper, ItemStack is) {
        final int size = hopper.getSizeInventory();
        for (int i = 0; i < size; i++) {
            ItemStack space = hopper.getStackInSlot(i);
            if (space == null) {
                return i;
            }
            if (space.stackSize < Math.min(space.getMaxStackSize(), hopper.getInventoryStackLimit()))
                if (ItemStack.areItemStacksEqual(oneSizedCopy(is), oneSizedCopy(space))) {
                    return i;
                }
        }
        return -1;
    }

    private static ItemStack oneSizedCopy(ItemStack is) {
        is = is.copy();
        is.stackSize = 1;
        return is;
    }
}
