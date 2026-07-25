package com.mitchej123.hodgepodge.mixins.early.minecraft;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.gen.FlatLayerInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(targets = "net.minecraft.client.gui.GuiCreateFlatWorld$Details")
public class MixinGuiCreateFlatWorld_FixItemCrash {

    /**
     * Setting a layer with no registered ItemBlock produces an NPE. Need to bypass and set name.
     */
    @ModifyVariable(method = "drawSlot", at = @At(value = "STORE"), name = "itemstack")
    private ItemStack hodgepodge$nullIfMissingItemBlock(ItemStack itemstack) {
        return itemstack != null && itemstack.getItem() == null ? null : itemstack;
    }

    @ModifyVariable(method = "drawSlot", at = @At(value = "STORE"), name = "s")
    private String hodgepodge$fixMissingItemBlockLabel(String s, @Local(name = "flatlayerinfo") FlatLayerInfo flatlayerinfo) {
        Block block = flatlayerinfo.func_151536_b();
        if (block == Blocks.air || Item.getItemFromBlock(block) != null) {
            return s;
        }
        return block.getLocalizedName();
    }
}
