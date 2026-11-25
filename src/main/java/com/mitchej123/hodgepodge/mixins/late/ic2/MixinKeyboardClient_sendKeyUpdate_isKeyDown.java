package com.mitchej123.hodgepodge.mixins.late.ic2;

import net.minecraft.client.settings.KeyBinding;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import ic2.core.util.KeyboardClient;

@Mixin(value = KeyboardClient.class)
public class MixinKeyboardClient_sendKeyUpdate_isKeyDown {

    @Redirect(
            method = "sendKeyUpdate",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/settings/GameSettings;isKeyDown(Lnet/minecraft/client/settings/KeyBinding;)Z",
                    remap = true),
            require = 1)
    private boolean hodgepodge$redirectToGetIsKeyPressed(KeyBinding keyBinding) {
        return keyBinding.getIsKeyPressed();
    }
}
