package com.mitchej123.hodgepodge.mixins.late.automagy;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import tuhljin.automagy.lib.AutomagyKeyHandler;

@Mixin(value = AutomagyKeyHandler.class, remap = false)
public class MixinAutomagyKeyHandler {

    @ModifyConstant(method = "<init>", constant = @Constant(stringValue = "key.categories.misc"), remap = false)
    private String hodgepodge$ChangeKeybindCategory(String original) {
        return "Thaumcraft";
    }

}
