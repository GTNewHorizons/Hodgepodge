package com.mitchej123.hodgepodge.mixins.early.fml;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mitchej123.hodgepodge.mixins.hooks.ASMDataStringPooler;

import cpw.mods.fml.common.discovery.asm.ModAnnotation;

@Mixin(value = ModAnnotation.class, remap = false)
public class MixinModAnnotation_Intern {

    @Definition(id = "put", method = "Ljava/util/Map;put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;")
    @Definition(id = "values", field = "Lcpw/mods/fml/common/discovery/asm/ModAnnotation;values:Ljava/util/Map;")
    @Expression("this.values.put(@(?), ?)")
    @ModifyExpressionValue(
            method = { "addProperty", "addEnumProperty", "endArray" },
            at = @At("MIXINEXTRAS:EXPRESSION"))
    private String intern(String original) {
        return ASMDataStringPooler.intern(original);
    }
}
