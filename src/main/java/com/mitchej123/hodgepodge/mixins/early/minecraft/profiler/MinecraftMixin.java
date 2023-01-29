package com.mitchej123.hodgepodge.mixins.early.minecraft.profiler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;

import com.mitchej123.hodgepodge.client.ClientTicker;

@Mixin(Minecraft.class)
public class MinecraftMixin {

    @Redirect(
            method = "displayDebugInfo",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/FontRenderer;drawStringWithShadow(Ljava/lang/String;III)I",
                    ordinal = 0),
            slice = @Slice(from = @At(value = "CONSTANT", args = { "stringValue=[?] " })))
    public int hodgepodge$drawLongString(FontRenderer fontRenderer, String text, int x, int y, int color) {
        int offset = ClientTicker.INSTANCE.getDebugPieTextOffset();

        int length = text.length();
        if (length >= 42) {
            int first = offset % length;
            text = text.substring(first) + " " + text.substring(0, first);

            text = text.substring(0, 43);
        }

        return fontRenderer.drawStringWithShadow(text, x, y, color);
    }
}
