package com.mitchej123.hodgepodge.mixins.early.fml;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mitchej123.hodgepodge.mixins.hooks.ASMDataStringPooler;

import cpw.mods.fml.common.discovery.asm.ModAnnotation;

@Mixin(value = ModAnnotation.EnumHolder.class, remap = false)
public class MixinEnumHolder_Intern {

    @Definition(
            id = "desc",
            field = "Lcpw/mods/fml/common/discovery/asm/ModAnnotation$EnumHolder;desc:Ljava/lang/String;")
    @Definition(
            id = "value",
            field = "Lcpw/mods/fml/common/discovery/asm/ModAnnotation$EnumHolder;value:Ljava/lang/String;")
    @Expression({ "this.desc = @(?)", "this.value = @(?)" })
    @ModifyExpressionValue(method = "<init>", at = @At("MIXINEXTRAS:EXPRESSION"))
    private String intern(String original) {
        return ASMDataStringPooler.intern(original);
    }
}
