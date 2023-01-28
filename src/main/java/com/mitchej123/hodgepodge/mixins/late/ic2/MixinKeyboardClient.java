package com.mitchej123.hodgepodge.mixins.late.ic2;

import ic2.core.util.KeyboardClient;
import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(KeyboardClient.class)
public class MixinKeyboardClient {

    @ModifyConstant(
            method = "<init>",
            constant = {
                @Constant(intValue = 29),
                @Constant(intValue = 45),
                @Constant(intValue = 46),
                @Constant(intValue = 50),
                @Constant(intValue = 56)
            })
    private int hodgepodge$modifykeycode(int original) {
        return Keyboard.KEY_NONE;
    }
}
