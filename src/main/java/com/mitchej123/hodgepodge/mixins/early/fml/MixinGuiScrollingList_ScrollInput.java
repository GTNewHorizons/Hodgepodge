package com.mitchej123.hodgepodge.mixins.early.fml;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mitchej123.hodgepodge.client.scrolllist.IScrollList;
import com.mitchej123.hodgepodge.client.scrolllist.ScrollListInput;

import cpw.mods.fml.client.GuiScrollingList;

@Mixin(GuiScrollingList.class)
public abstract class MixinGuiScrollingList_ScrollInput {

    @Redirect(
            method = "drawScreen",
            at = @At(value = "INVOKE", target = "Lorg/lwjgl/input/Mouse;isButtonDown(I)Z", remap = false),
            remap = false)
    private boolean hodgepodge$noPolledButton(int button) {
        return false;
    }

    @Redirect(
            method = "drawScreen",
            at = @At(value = "INVOKE", target = "Lorg/lwjgl/input/Mouse;next()Z", remap = false),
            remap = false)
    private boolean hodgepodge$noPolledEvents() {
        return false;
    }

    @Inject(method = "drawScreen", at = @At("HEAD"), remap = false)
    private void hodgepodge$onDraw(int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        ScrollListInput.onDraw((IScrollList) this, mouseY);
    }
}
