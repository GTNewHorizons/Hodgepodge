package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.nbt.NBTTagString;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mitchej123.hodgepodge.util.StringPooler;

@Mixin(NBTTagString.class)
public class MixinNBTTagString_stringPooler {

    @ModifyExpressionValue(
            method = "func_152446_a",
            at = @At(value = "INVOKE", target = "Ljava/io/DataInput;readUTF()Ljava/lang/String;"))
    private String poolString(String s) {
        return StringPooler.INSTANCE.getString(s);
    }
}
