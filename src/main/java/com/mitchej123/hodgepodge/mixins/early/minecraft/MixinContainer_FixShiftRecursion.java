package com.mitchej123.hodgepodge.mixins.early.minecraft;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;

@Mixin(Container.class)
public abstract class MixinContainer_FixShiftRecursion {

    @Shadow
    /* dragEvent */
    private int field_94536_g;

    @Shadow
    public List<Slot> inventorySlots;

    @Shadow
    public abstract ItemStack transferStackInSlot(EntityPlayer player, int index);

    @WrapMethod(method = "slotClick")
    public ItemStack hodgepodge$slotClickWrap(int slotId, int clickedButton, int mode, EntityPlayer player,
            Operation<ItemStack> original) {
        if (field_94536_g == 0 && mode == 1 && (clickedButton == 0 || clickedButton == 1)) {
            if (slotId < 0) {
                return null;
            }

            Slot targetSlot = this.inventorySlots.get(slotId);

            if (targetSlot == null || !targetSlot.canTakeStack(player)) {
                return null;
            }

            ItemStack tempStack = this.transferStackInSlot(player, slotId);
            if (tempStack == null) return null;
            ItemStack itemstack = tempStack.copy();
            while (tempStack != null && hodgepodge$areItemsEqual(targetSlot.getStack(), tempStack)) {
                tempStack = this.transferStackInSlot(player, slotId);
            }

            return itemstack;
        }

        return original.call(slotId, clickedButton, mode, player);
    }

    @Unique
    private static boolean hodgepodge$areItemsEqual(ItemStack stackA, ItemStack stackB) {
        if (stackA == stackB) {
            return true;
        } else {
            return stackA != null && stackB != null && stackA.isItemEqual(stackB);
        }
    }

}
