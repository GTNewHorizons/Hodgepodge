package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.nbt.NBTTagString;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mitchej123.hodgepodge.util.StringPooler;

@Mixin(NBTTagString.class)
public class MixinNBTTagString_stringPooler {

    @Shadow
    private String data;

    @Inject(method = "Lnet/minecraft/nbt/NBTTagString;<init>(Ljava/lang/String;)V", at = @At("RETURN"))
    private void poolStringInit(CallbackInfo ci) {
        this.data = StringPooler.INSTANCE.getString(this.data);
    }

    @ModifyExpressionValue(
            method = "func_152446_a",
            at = @At(value = "INVOKE", target = "Ljava/io/DataInput;readUTF()Ljava/lang/String;"))
    private String poolStringLoad(String s) {
        return StringPooler.INSTANCE.getString(s);
    }
}
