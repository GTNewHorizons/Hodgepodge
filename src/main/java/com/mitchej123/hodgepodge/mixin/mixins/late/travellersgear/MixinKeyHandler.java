package com.mitchej123.hodgepodge.mixin.mixins.late.travellersgear;

import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import travellersgear.TravellersGear;
import travellersgear.client.KeyHandler;

@Mixin(KeyHandler.class)
public class MixinKeyHandler {

    @ModifyConstant(method = "<clinit>", constant = { @Constant(intValue = 19), @Constant(intValue = 71) })
    private static int hodgepodge$modifykeycode(int original) {
        return Keyboard.KEY_NONE;
    }

    @ModifyConstant(method = "<clinit>", constant = @Constant(stringValue = "key.categories.inventory"))
    private static String hodgepodge$modifyKeybindCategory(String original) {
        return TravellersGear.MODNAME;
    }
}
