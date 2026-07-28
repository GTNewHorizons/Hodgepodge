package com.mitchej123.hodgepodge.mixins.late.bibliocraft;

import net.minecraft.item.Item;
import net.minecraft.item.ItemEditableBook;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import org.spongepowered.asm.mixin.Mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;

import jds.bibliocraft.items.ItemAtlas;
import jds.bibliocraft.items.ItemBigBook;
import jds.bibliocraft.items.ItemLoader;
import jds.bibliocraft.items.ItemPlate;
import jds.bibliocraft.items.ItemRecipeBook;
import jds.bibliocraft.items.ItemStockroomCatalog;
import jds.bibliocraft.tileentities.TileEntityTypeMachine;

@Mixin(TileEntityTypeMachine.class)
public class MixinBibliocraftTypesettingAutomation {

    @WrapMethod(method = "isItemValidForSlot")
    private boolean hodgepodge$allowAllValidInputTypes(int slot, ItemStack itemstack, Operation<Boolean> original) {
        NBTTagCompound nbt;
        Item stackItem = itemstack.getItem();
        if (slot == 0) return stackItem instanceof ItemPlate || stackItem instanceof ItemEditableBook || stackItem instanceof ItemEnchantedBook || stackItem instanceof ItemStockroomCatalog || ((stackItem instanceof ItemBigBook || stackItem instanceof ItemRecipeBook) && (nbt = itemstack.getTagCompound()) != null && nbt.getBoolean("signed")) || stackItem instanceof ItemAtlas && itemstack.getTagCompound() != null;
        return original.call(slot, itemstack);
    }

}
