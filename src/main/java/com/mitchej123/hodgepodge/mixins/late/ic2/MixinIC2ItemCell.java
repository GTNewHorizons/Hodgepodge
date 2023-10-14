package com.mitchej123.hodgepodge.mixins.late.ic2;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;

import ic2.core.Ic2Items;
import ic2.core.item.resources.ItemCell;

@Mixin(value = ItemCell.class)
abstract public class MixinIC2ItemCell extends Item {

    @Override
    public boolean hasContainerItem(ItemStack itemStack) {
        return (itemStack.getItemDamage() != 0);
    }

    @Override
    public ItemStack getContainerItem(ItemStack itemStack) {
        if (hasContainerItem(itemStack)) return Ic2Items.cell.copy();
        return null;
    }
}
