package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MixinMinecraft_ClearStaleLoadingScreenInput {

    @Inject(method = "startGame", at = @At("TAIL"))
    private void startGame(CallbackInfo info) {
        while (Mouse.next()) {
            while (Mouse.next()) {}
            Mouse.poll();
        }

        while (Keyboard.next()) {
            while (Keyboard.next()) {}
            Keyboard.poll();
        }

        KeyBinding.unPressAllKeys();
    }
}
