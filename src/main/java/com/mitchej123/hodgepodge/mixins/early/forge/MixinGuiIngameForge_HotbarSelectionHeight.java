package com.mitchej123.hodgepodge.mixins.early.forge;

import net.minecraftforge.client.GuiIngameForge;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(GuiIngameForge.class)
public class MixinGuiIngameForge_HotbarSelectionHeight {

    /**
     * The selection frame in widgets.png is 24x24 (at u=0, v=22), but Forge draws it only 22 pixels tall, cutting off
     * its bottom border. Only the selection frame call is changed, the 182x22 hotbar background is left alone.
     */
    @ModifyArg(
            method = "renderHotbar",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraftforge/client/GuiIngameForge;drawTexturedModalRect(IIIIII)V",
                    remap = true),
            index = 5,
            remap = false)
    private int hodgepodge$fixSelectionHeight(int x, int y, int textureX, int textureY, int width, int height) {
        return textureY == 22 && height == 22 ? 24 : height;
    }
}
