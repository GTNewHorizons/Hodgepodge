package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.client.renderer.tileentity.TileEntitySignRenderer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

/**
 * Append {@code §r} before the trailing {@code " <"} cursor marker so the line's active style does not bleed into it.
 * Vanilla centers via {@code drawString(s, -getStringWidth(s) / 2, ...)}, so both args must get the reset. Width-only or
 * draw-only and the line mis-centers, since bold widens the space and {@code <}.
 */
@Mixin(TileEntitySignRenderer.class)
public class MixinTileEntitySignRenderer_CursorReset {

    @Unique
    private static String hodgepodge$resetCursor(String s) {
        if (s.endsWith(" <")) {
            return s.substring(0, s.length() - 2) + "§r <";
        }
        return s;
    }

    @ModifyArg(
            method = "renderTileEntityAt(Lnet/minecraft/tileentity/TileEntitySign;DDDF)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/FontRenderer;drawString(Ljava/lang/String;III)I",
                    ordinal = 0),
            index = 0)
    private String hodgepodge$resetCursorColorDraw(String s) {
        return hodgepodge$resetCursor(s);
    }

    @ModifyArg(
            method = "renderTileEntityAt(Lnet/minecraft/tileentity/TileEntitySign;DDDF)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/FontRenderer;getStringWidth(Ljava/lang/String;)I",
                    ordinal = 0),
            index = 0)
    private String hodgepodge$resetCursorColorWidth(String s) {
        return hodgepodge$resetCursor(s);
    }
}
