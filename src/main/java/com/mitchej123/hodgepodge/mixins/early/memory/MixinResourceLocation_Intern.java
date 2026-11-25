package com.mitchej123.hodgepodge.mixins.early.memory;

import net.minecraft.util.ResourceLocation;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

@Mixin(ResourceLocation.class)
public class MixinResourceLocation_Intern {

    @Definition(id = "resourceDomain", field = "Lnet/minecraft/util/ResourceLocation;resourceDomain:Ljava/lang/String;")
    @Expression("this.resourceDomain = @(?)")
    @ModifyExpressionValue(
            method = { "<init>(Ljava/lang/String;)V", "<init>(Ljava/lang/String;Ljava/lang/String;)V" },
            at = @At("MIXINEXTRAS:EXPRESSION"))
    private String intern(String original) {
        return original.intern();
    }
}
