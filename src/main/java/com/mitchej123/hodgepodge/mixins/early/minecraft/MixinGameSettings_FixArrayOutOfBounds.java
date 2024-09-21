package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.EnumChatFormatting;

import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GameSettings.class)
public class MixinGameSettings_FixArrayOutOfBounds {

    @Inject(method = "getKeyDisplayString", at = @At("HEAD"), cancellable = true)
    private static void hodgepodge$fixArrayOutOFBounds(int keycode, CallbackInfoReturnable<String> cir) {
        if (keycode > Keyboard.KEYBOARD_SIZE - 1) {
            cir.setReturnValue(EnumChatFormatting.RED + "ERROR");
        }
    }

}
