package com.mitchej123.hodgepodge.mixins.early.minecraft;

import java.util.List;

import net.minecraft.client.gui.FontRenderer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = FontRenderer.class, priority = 1500)
public abstract class MixinFontRenderer_NewlineSupport {

    @Shadow
    public int FONT_HEIGHT;

    @Shadow
    public abstract List<String> listFormattedStringToWidth(String str, int wrapWidth);

    @Shadow
    public abstract int getStringWidth(String text);

    @Shadow
    public abstract int drawString(String text, int x, int y, int color, boolean dropShadow);

    @Inject(
            method = "drawString(Ljava/lang/String;IIIZ)I",
            at = @At("HEAD"),
            cancellable = true)
    private void hodgepodge$splitNewlines(String text, int x, int y, int color, boolean dropShadow,
            CallbackInfoReturnable<Integer> cir) {
        if (text == null || text.indexOf('\n') < 0) return;

        List<String> lines = this.listFormattedStringToWidth(text, Integer.MAX_VALUE);
        int maxX = x;
        for (int i = 0; i < lines.size(); i++) {
            maxX = Math.max(maxX, this.drawString(lines.get(i), x, y + i * this.FONT_HEIGHT, color, dropShadow));
        }
        cir.setReturnValue(maxX);
    }

    @Inject(
            method = "getStringWidth",
            at = @At("HEAD"),
            cancellable = true)
    private void hodgepodge$measureNewlines(String text, CallbackInfoReturnable<Integer> cir) {
        if (text == null || text.indexOf('\n') < 0) return;

        List<String> lines = this.listFormattedStringToWidth(text, Integer.MAX_VALUE);
        int maxWidth = 0;
        for (String line : lines) {
            maxWidth = Math.max(maxWidth, this.getStringWidth(line));
        }
        cir.setReturnValue(maxWidth);
    }
}
