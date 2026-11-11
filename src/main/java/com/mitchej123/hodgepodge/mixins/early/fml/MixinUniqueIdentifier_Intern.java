package com.mitchej123.hodgepodge.mixins.early.fml;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import cpw.mods.fml.common.registry.GameRegistry;

@Mixin(value = GameRegistry.UniqueIdentifier.class, remap = false)
public class MixinUniqueIdentifier_Intern {

    @Definition(
            id = "modId",
            field = "Lcpw/mods/fml/common/registry/GameRegistry$UniqueIdentifier;modId:Ljava/lang/String;")
    @Expression("this.modId = @(?)")
    @ModifyExpressionValue(
            method = { "<init>(Ljava/lang/String;)V", "<init>(Ljava/lang/String;Ljava/lang/String;)V" },
            at = @At("MIXINEXTRAS:EXPRESSION"))
    private String intern(String original) {
        return original.intern();
    }
}
