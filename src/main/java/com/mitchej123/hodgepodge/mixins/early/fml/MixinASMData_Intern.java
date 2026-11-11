package com.mitchej123.hodgepodge.mixins.early.fml;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mitchej123.hodgepodge.mixins.hooks.ASMDataStringPooler;

import cpw.mods.fml.common.discovery.ASMDataTable;

@Mixin(value = ASMDataTable.ASMData.class, remap = false)
public class MixinASMData_Intern {

    @Definition(
            id = "annotationName",
            field = "Lcpw/mods/fml/common/discovery/ASMDataTable$ASMData;annotationName:Ljava/lang/String;")
    @Definition(
            id = "className",
            field = "Lcpw/mods/fml/common/discovery/ASMDataTable$ASMData;className:Ljava/lang/String;")
    @Definition(
            id = "objectName",
            field = "Lcpw/mods/fml/common/discovery/ASMDataTable$ASMData;objectName:Ljava/lang/String;")
    @Expression({ "this.annotationName = @(?)", "this.className = @(?)", "this.objectName = @(?)" })
    @ModifyExpressionValue(method = "<init>", at = @At("MIXINEXTRAS:EXPRESSION"))
    private String intern(String original) {
        return ASMDataStringPooler.intern(original);
    }
}
