package com.mitchej123.hodgepodge.mixins.early.cofhcore;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import cofh.core.util.nbt.NBTTagSmartByteArray;

@Mixin(NBTTagSmartByteArray.class)
public abstract class MixinNBTTagSmartByteArray {

    /**
     * @author koolkrafter5
     * @reason Passing null to NBTTagByteArray prevents chunks from saving
     */
    @ModifyArg(
            method = "<init>(I)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/NBTTagByteArray;<init>([B)V"))
    private static byte[] fixNullByteArray(byte[] p_i45128_1_) {
        return new byte[0];
    }
}
