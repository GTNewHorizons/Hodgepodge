package com.mitchej123.hodgepodge.mixins.late.thaumcraft;

import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import thaumcraft.common.lib.events.KeyHandler;

@Mixin(KeyHandler.class)
public class MixinKeyHandlerThaumcraft {

    @ModifyConstant(method = "<init>", constant = @Constant(stringValue = "key.categories.misc"))
    private String hodgepodge$ChangeKeybindCategory(String original) {
        return "Thaumcraft";
    }

    @ModifyConstant(
            method = "<init>",
            constant = { @Constant(intValue = 33), @Constant(intValue = 34), @Constant(intValue = 35) })
    private int hodgepodge$UnbindKeybind(int original) {
        return Keyboard.KEY_NONE;
    }

}
