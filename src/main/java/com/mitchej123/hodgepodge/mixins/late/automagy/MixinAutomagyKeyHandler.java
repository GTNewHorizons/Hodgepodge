package com.mitchej123.hodgepodge.mixins.late.automagy;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import tuhljin.automagy.lib.AutomagyKeyHandler;

@Mixin(AutomagyKeyHandler.class)
public class MixinAutomagyKeyHandler {

    @ModifyConstant(method = "<init>", constant = @Constant(stringValue = "key.categories.misc"))
    private String hodgepodge$ChangeKeybindCategory(String original) {
        return "Thaumcraft";
    }

}
