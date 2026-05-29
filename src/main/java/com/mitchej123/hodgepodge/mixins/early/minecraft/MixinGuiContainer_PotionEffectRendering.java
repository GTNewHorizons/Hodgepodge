package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.client.gui.inventory.GuiContainer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mitchej123.hodgepodge.client.IHodgepodgePotionPanelRenderer;

/**
 * Injects the potion panel render into GuiContainer.drawScreen immediately after the drawGuiContainerForegroundLayer
 * call. This fires for every InventoryEffectRenderer subclass regardless of what it overrides, and places the panel
 * correctly before the cursor item and tooltips.
 */
@Mixin(GuiContainer.class)
public abstract class MixinGuiContainer_PotionEffectRendering {

    @Inject(
            method = "drawScreen",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/inventory/GuiContainer;drawGuiContainerForegroundLayer(II)V",
                    shift = At.Shift.AFTER))
    private void hodgepodge$renderPotionPanelAfterForeground(int mouseX, int mouseY, float partialTicks,
            CallbackInfo ci) {
        if (this instanceof IHodgepodgePotionPanelRenderer) {
            ((IHodgepodgePotionPanelRenderer) this).hodgepodge$renderPotionPanelForeground();
        }
    }
}
