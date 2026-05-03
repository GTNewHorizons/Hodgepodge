package com.mitchej123.hodgepodge.mixins.early.minecraft;

import java.util.List;

import net.minecraft.client.gui.FontRenderer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;

@Mixin(value = FontRenderer.class, priority = 1500)
public abstract class MixinFontRenderer_NewlineSupport {

    @Shadow
    public int FONT_HEIGHT;

    @Shadow
    public abstract List<String> listFormattedStringToWidth(String str, int wrapWidth);

    // @WrapMethod sits outside the @Inject layer — this runs before any cancellable HEAD inject
    // from other mods on the same method (Angelica's BatchingFontRenderer inject in particular).
    // Priority-based ordering of competing HEAD injects isn't reliable in mixed environments, so
    // wrapping the method is the deterministic way to see the \n before anyone rewrites dispatch.
    @WrapMethod(method = "drawString(Ljava/lang/String;IIIZ)I")
    private int hodgepodge$splitNewlinesDraw(String text, int x, int y, int color, boolean dropShadow,
            Operation<Integer> delegate) {
        if (text == null || text.indexOf('\n') < 0) {
            return delegate.call(text, x, y, color, dropShadow);
        }

        List<String> lines = this.listFormattedStringToWidth(text, Integer.MAX_VALUE);
        int maxX = x;
        for (int i = 0; i < lines.size(); i++) {
            maxX = Math.max(maxX, delegate.call(lines.get(i), x, y + i * this.FONT_HEIGHT, color, dropShadow));
        }
        return maxX;
    }

    @WrapMethod(method = "getStringWidth")
    private int hodgepodge$splitNewlinesWidth(String text, Operation<Integer> delegate) {
        if (text == null || text.indexOf('\n') < 0) {
            return delegate.call(text);
        }

        List<String> lines = this.listFormattedStringToWidth(text, Integer.MAX_VALUE);
        int maxWidth = 0;
        for (String line : lines) {
            maxWidth = Math.max(maxWidth, delegate.call(line));
        }
        return maxWidth;
    }
}
