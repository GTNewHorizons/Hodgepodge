package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.client.gui.GuiScreen;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mitchej123.hodgepodge.client.scrolllist.ScrollListInput;

@Mixin(GuiScreen.class)
public class MixinGuiScreen_ScrollInput {

    @Inject(method = "handleMouseInput", at = @At("HEAD"))
    private void hodgepodge$scrollListMouseEvent(CallbackInfo ci) {
        ScrollListInput.onMouseEvent((GuiScreen) (Object) this);
    }

    @Inject(
            method = "handleMouseInput",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiScreen;mouseClicked(III)V",
                    shift = At.Shift.AFTER))
    private void hodgepodge$scrollListMousePressed(CallbackInfo ci) {
        ScrollListInput.onMousePressed((GuiScreen) (Object) this);
    }
}
