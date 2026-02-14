package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.settings.KeyBinding;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MixinMinecraft_ClearStaleLoadingScreenInput {

    @Unique
    private long hodgepodge$suppressInputUntil = 0;

    @Inject(method = "displayGuiScreen", at = @At("TAIL"))
    private void hodgepodge$markClearInputOnMainMenu(GuiScreen guiScreenIn, CallbackInfo ci) {
        Minecraft minecraft = (Minecraft) (Object) this;
        if (minecraft.currentScreen instanceof GuiMainMenu) {
            // keep draining for 500ms, waiting for Display.update() to flush (needed for lwjgl2)
            hodgepodge$suppressInputUntil = Minecraft.getSystemTime() + 500;
        }
    }

    @Inject(method = "runTick", at = @At("HEAD"))
    private void hodgepodge$clearStaleInput(CallbackInfo ci) {
        if (Minecraft.getSystemTime() < hodgepodge$suppressInputUntil) {
            while (Mouse.next()) {}
            while (Keyboard.next()) {}
            KeyBinding.unPressAllKeys();
        }
    }
}
