package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.client.renderer.tileentity.TileEntitySignRenderer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(TileEntitySignRenderer.class)
public class MixinTileEntitySignRenderer_CursorReset {

    @ModifyArg(
            method = "renderTileEntityAt(Lnet/minecraft/tileentity/TileEntitySign;DDDF)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/FontRenderer;drawString(Ljava/lang/String;III)I",
                    ordinal = 0),
            index = 0)
    private String hodgepodge$resetCursorColor(String s) {
        if (s.endsWith(" <")) {
            return s.substring(0, s.length() - 2) + "§r <";
        }
        return s;
    }
}
