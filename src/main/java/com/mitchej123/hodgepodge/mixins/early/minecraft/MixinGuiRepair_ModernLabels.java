package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiRepair;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.Container;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;

/**
 * Makes the anvil labels look like modern vanilla: adds the "Inventory" label and draws the cost text with a drop
 * shadow on a translucent box. The cost text outline is skipped by always taking the unicode branch and cancelling its
 * drawRect calls.
 */
@Mixin(GuiRepair.class)
public abstract class MixinGuiRepair_ModernLabels extends GuiContainer {

    private MixinGuiRepair_ModernLabels(Container container) {
        super(container);
    }

    @Inject(
            method = "drawGuiContainerForegroundLayer",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/FontRenderer;drawString(Ljava/lang/String;III)I",
                    ordinal = 0,
                    shift = At.Shift.AFTER))
    private void hodgepodge$drawInventoryLabel(int mouseX, int mouseY, CallbackInfo ci) {
        fontRendererObj.drawString(I18n.format("container.inventory"), 8, ySize - 94, 4210752);
    }

    @ModifyExpressionValue(
            method = "drawGuiContainerForegroundLayer",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/FontRenderer;getUnicodeFlag()Z"))
    private boolean hodgepodge$skipCostOutline(boolean original) {
        return true;
    }

    @WrapWithCondition(
            method = "drawGuiContainerForegroundLayer",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiRepair;drawRect(IIIII)V"))
    private boolean hodgepodge$skipCostBackground(int left, int top, int right, int bottom, int color) {
        return false;
    }

    @Redirect(
            method = "drawGuiContainerForegroundLayer",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/FontRenderer;drawString(Ljava/lang/String;III)I",
                    ordinal = 4))
    private int hodgepodge$drawCostText(FontRenderer fontRenderer, String text, int x, int y, int color) {
        Gui.drawRect(x - 2, y - 2, x + fontRenderer.getStringWidth(text) + 2, y + 10, 0x4F000000);
        return fontRenderer.drawStringWithShadow(text, x, y, color);
    }
}
