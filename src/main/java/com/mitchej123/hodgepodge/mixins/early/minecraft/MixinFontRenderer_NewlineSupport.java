package com.mitchej123.hodgepodge.mixins.early.minecraft;

import java.util.List;

import net.minecraft.client.gui.FontRenderer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FontRenderer.class)
public abstract class MixinFontRenderer_NewlineSupport {

    @Shadow
    public int FONT_HEIGHT;

    @Shadow
    public abstract List<String> listFormattedStringToWidth(String str, int wrapWidth);

    @Shadow
    private int renderString(String text, int x, int y, int color, boolean shadow) {
        return 0;
    }

    @Inject(method = "renderString", at = @At("HEAD"), cancellable = true)
    private void hodgepodge$handleNewlines(String text, int x, int y, int color, boolean shadow,
            CallbackInfoReturnable<Integer> cir) {
        if (text == null || text.indexOf('\n') < 0) return;

        List<String> lines = this.listFormattedStringToWidth(text, Integer.MAX_VALUE);
        int lastX = 0;
        for (int i = 0; i < lines.size(); i++) {
            lastX = this.renderString(lines.get(i), x, y + i * this.FONT_HEIGHT, color, shadow);
        }
        cir.setReturnValue(lastX);
    }
}
