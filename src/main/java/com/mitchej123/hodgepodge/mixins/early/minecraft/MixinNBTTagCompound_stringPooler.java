package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.nbt.NBTTagCompound;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mitchej123.hodgepodge.util.StringPooler;

@Mixin(NBTTagCompound.class)
public class MixinNBTTagCompound_stringPooler {

    @ModifyExpressionValue(
            method = "func_152448_b",
            at = @At(value = "INVOKE", target = "Ljava/io/DataInput;readUTF()Ljava/lang/String;"))
    private static String poolString(String s) {
        return StringPooler.INSTANCE.getString(s);
    }
}
