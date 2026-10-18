package com.mitchej123.hodgepodge.mixins.late.witchery;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import com.emoniph.witchery.client.ClientEvents;

@Mixin(value = ClientEvents.GUIOverlay.class, remap = false)
public class MixinClientEvents_HotbarSelectionHeight {

    /**
     * Witchery copies Forge's hotbar code for the vampire power bar, including the selection frame being drawn 2 pixels
     * too short (see MixinGuiIngameForge_HotbarSelectionHeight). There are two selection frame calls, one for a
     * selected vampire power and one for the selected item slot.
     */
    @ModifyArg(
            method = "renderHotbar",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/emoniph/witchery/client/ClientEvents$GUIOverlay;drawTexturedModalRect(IIIIII)V",
                    remap = true),
            index = 5)
    private int hodgepodge$fixSelectionHeight(int x, int y, int textureX, int textureY, int width, int height) {
        return textureY == 22 && height == 22 ? 24 : height;
    }
}
