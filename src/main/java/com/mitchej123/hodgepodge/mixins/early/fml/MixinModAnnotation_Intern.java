package com.mitchej123.hodgepodge.mixins.early.fml;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

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

    @Definition(id = "member", field = "Lcpw/mods/fml/common/discovery/asm/ModAnnotation;member:Ljava/lang/String;")
    @Expression("this.member = @(?)")
    @ModifyExpressionValue(
            method = "<init>(Lcpw/mods/fml/common/discovery/asm/ASMModParser$AnnotationType;Lorg/objectweb/asm/Type;Ljava/lang/String;)V",
            at = @At("MIXINEXTRAS:EXPRESSION"))
    private String intern2(String original) {
        return ASMDataStringPooler.intern(original);
    }

    @ModifyVariable(method = "addProperty", at = @At("HEAD"), argsOnly = true, name = "arg2")
    private Object intern2(Object original) {
        if (original instanceof String s) {
            return ASMDataStringPooler.intern(s);
        }
        return original;
    }
}
