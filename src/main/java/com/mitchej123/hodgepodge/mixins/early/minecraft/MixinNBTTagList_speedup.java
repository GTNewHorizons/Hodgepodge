package com.mitchej123.hodgepodge.mixins.early.minecraft;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagList;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.sugar.Local;
import com.mitchej123.hodgepodge.mixins.interfaces.NBTTagListExt;

@Mixin(NBTTagList.class)
public class MixinNBTTagList_speedup implements NBTTagListExt {

    @Shadow
    private List tagList;

    @Inject(
            method = "copy",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/List;iterator()Ljava/util/Iterator;",
                    shift = At.Shift.AFTER))
    public void copyEnsureCapacity(CallbackInfoReturnable<NBTBase> cir, @Local NBTTagList nbttaglist) {
        // Ensure the capacity of the new tag list is the same as the old one so we don't need to resize it later
        ((NBTTagListExt) nbttaglist).ensureCapacity(tagList.size());

    }

    @Override
    public void ensureCapacity(int capacity) {
        if (tagList instanceof ArrayList tagArrayList) {
            tagArrayList.ensureCapacity(capacity);
        }
    }
}
