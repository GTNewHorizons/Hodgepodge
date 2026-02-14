package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.settings.KeyBinding;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MixinMinecraft_ClearStaleLoadingScreenInput {
    @Inject(method = "displayGuiScreen", at = @At("TAIL"))
    private void hodgepodge$clearBufferedInputOnMainMenu(GuiScreen guiScreenIn, CallbackInfo ci) {
        Minecraft minecraft = (Minecraft) (Object) this;
        if (!(minecraft.currentScreen instanceof GuiMainMenu)) {
            return;
        }

        // Consume stale events queued in the loading screen
        while (Mouse.next()) {}
        while (Keyboard.next()) {}

        KeyBinding.unPressAllKeys();
    }
}
