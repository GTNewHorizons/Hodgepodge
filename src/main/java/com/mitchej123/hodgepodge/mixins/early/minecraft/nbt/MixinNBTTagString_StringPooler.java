package com.mitchej123.hodgepodge.mixins.early.minecraft.nbt;

import net.minecraft.nbt.NBTTagString;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mitchej123.hodgepodge.util.StringPooler;

@Mixin(NBTTagString.class)
public class MixinNBTTagString_StringPooler {

    @Definition(id = "data", field = "Lnet/minecraft/nbt/NBTTagString;data:Ljava/lang/String;")
    @Expression("this.data = @(?)")
    @ModifyExpressionValue(
            method = { "<init>(Ljava/lang/String;)V", "func_152446_a" },
            at = @At("MIXINEXTRAS:EXPRESSION"))
    private String poolString(String original) {
        return StringPooler.INSTANCE.getString(original);
    }
}
