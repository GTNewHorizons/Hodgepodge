package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(InventoryCrafting.class)
public class MixinInventoryCrafting {

    @Shadow
    private ItemStack[] stackList;

    @Shadow
    private Container eventHandler;

    /**
     * @author kuba6000
     * @reason Optimize InventoryCrafting
     */
    @Overwrite
    public void setInventorySlotContents(int index, ItemStack stack) {
        if (stack == null && this.stackList[index] == null) return;
        this.stackList[index] = stack;
        this.eventHandler.onCraftMatrixChanged((IInventory) this);
    }

}
