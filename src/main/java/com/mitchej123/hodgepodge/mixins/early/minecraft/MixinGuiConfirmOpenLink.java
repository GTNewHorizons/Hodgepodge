package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.client.gui.GuiConfirmOpenLink;
import net.minecraft.client.gui.GuiScreen;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(GuiConfirmOpenLink.class)
public class MixinGuiConfirmOpenLink extends GuiScreen {

    @ModifyArg(
            method = "initGui",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiButton;<init>(IIIIILjava/lang/String;)V",
                    ordinal = 0),
            index = 1)
    private int hodgepodge$fixButton0(int xPos) {
        return this.width / 2 - 50 - 105;
    }

    @ModifyArg(
            method = "initGui",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiButton;<init>(IIIIILjava/lang/String;)V",
                    ordinal = 1),
            index = 1)
    private int hodgepodge$fixButton1(int xPos) {
        return this.width / 2 - 50;
    }

    @ModifyArg(
            method = "initGui",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiButton;<init>(IIIIILjava/lang/String;)V",
                    ordinal = 2),
            index = 1)
    private int hodgepodge$fixButton2(int xPos) {
        return this.width / 2 - 50 + 105;
    }

}
