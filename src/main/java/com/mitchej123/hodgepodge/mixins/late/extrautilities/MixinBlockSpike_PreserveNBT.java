package com.mitchej123.hodgepodge.mixins.late.extrautilities;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import com.llamalad7.mixinextras.sugar.Local;
import com.rwtema.extrautils.block.BlockSpike;

@Mixin(BlockSpike.class)
public abstract class MixinBlockSpike_PreserveNBT {

    @ModifyVariable(
            method = "onBlockPlacedBy",
            name = "enchantments",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/rwtema/extrautils/tileentity/TileEntityEnchantedSpike;setEnchantmentTagList(Lnet/minecraft/nbt/NBTTagList;)V"))
    private NBTTagList hodgepodge$injectNBTCompoundIntoTagList(NBTTagList enchantments,
            @Local(name = "stack") ItemStack stack) {
        if (enchantments == null || enchantments.tagCount() == 0
                || stack.stackTagCompound == null
                || stack.stackTagCompound.hasNoTags()) {
            return enchantments;
        }

        NBTTagCompound injectedNBT = (NBTTagCompound) stack.stackTagCompound.copy();
        if (injectedNBT.hasKey("ench")) {
            injectedNBT.removeTag("ench");
            if (injectedNBT.hasNoTags()) return enchantments;
        }

        NBTTagCompound enchant = enchantments.getCompoundTagAt(0);
        enchant.setTag("hodgepodge_injectedNBT", injectedNBT);
        return enchantments;
    }
}
