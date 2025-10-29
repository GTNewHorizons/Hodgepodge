package com.mitchej123.hodgepodge.mixins.late.extrautilities;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.rwtema.extrautils.tileentity.TileEntityEnchantedSpike;

@Mixin(TileEntityEnchantedSpike.class)
public abstract class MixinTileEntityEnchantedSpike_PreserveNBT {

    @Unique
    private static final List<String> hodgepodge$IGNORED_TAGS = Arrays.asList("ench", "x", "y", "z", "id");

    @Unique
    private NBTTagCompound hodgepodge$preservedNBT;

    @WrapMethod(method = "setEnchantmentTagList", remap = false)
    private void hodgepodge$extractNBTCompoundFromTagList(NBTTagList enchants, Operation<Void> original) {
        if (enchants == null || enchants.tagCount() == 0) {
            original.call(enchants);
            return;
        }

        NBTTagCompound firstEnchant = enchants.getCompoundTagAt(0);
        if (!firstEnchant.hasKey("hodgepodge_injectedNBT")) {
            original.call(enchants);
            return;
        }

        this.hodgepodge$preservedNBT = firstEnchant.getCompoundTag("hodgepodge_injectedNBT");
        firstEnchant.removeTag("hodgepodge_injectedNBT");
        original.call(enchants);
    }

    @WrapMethod(method = "readFromNBT")
    private void hodgepodge$readFromNBT(NBTTagCompound nbt, Operation<Void> original) {
        original.call(nbt);
        if (nbt == null || nbt.hasNoTags()) return;

        NBTTagCompound preservedNBT = new NBTTagCompound();
        Set<String> preservedTags = nbt.func_150296_c();
        for (String tag : preservedTags) {
            if (hodgepodge$IGNORED_TAGS.contains(tag)) continue;
            preservedNBT.setTag(tag, nbt.getTag(tag));
        }

        if (preservedNBT.hasNoTags()) return;
        this.hodgepodge$preservedNBT = preservedNBT;
    }

    @WrapMethod(method = "writeToNBT")
    private void hodgepodge$writeToNBT(NBTTagCompound nbt, Operation<Void> original) {
        original.call(nbt);
        hodgepodge$injectPreservedTags(nbt);
    }

    @WrapMethod(method = "getEnchantmentTagList", remap = false)
    private NBTTagCompound hodgepodge$getEnchantmentTagList(Operation<NBTTagCompound> original) {
        NBTTagCompound nbt = original.call();
        hodgepodge$injectPreservedTags(nbt);
        return nbt;
    }

    @Unique
    private void hodgepodge$injectPreservedTags(NBTTagCompound nbt) {
        if (nbt == null || this.hodgepodge$preservedNBT == null || this.hodgepodge$preservedNBT.hasNoTags()) return;

        Set<String> preservedTags = this.hodgepodge$preservedNBT.func_150296_c();
        for (String tag : preservedTags) {
            if (hodgepodge$IGNORED_TAGS.contains(tag)) continue;
            nbt.setTag(tag, this.hodgepodge$preservedNBT.getTag(tag));
        }
    }
}
