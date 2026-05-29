package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.client.gui.FontRenderer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import com.mitchej123.hodgepodge.util.FontRenderingCompat;

/**
 * When Angelica is not installed, preprocesses text through GTNHLib's registered TextPreprocessor (the
 * LegacyColorFallback) before the vanilla FontRenderer handles it. This converts extended color codes (&amp;#RRGGBB,
 * &amp;c, etc.) to their nearest vanilla equivalents.
 *
 * When Angelica IS installed, its HEAD @Inject on drawString cancels before this code path is reached, so this mixin
 * effectively never fires.
 */
@Mixin(FontRenderer.class)
public class MixinFontRenderer_FallbackPreprocess {

    @ModifyVariable(method = "drawString(Ljava/lang/String;IIIZ)I", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private String hodgepodge$preprocessDrawString(String text) {
        return FontRenderingCompat.HAS_PREPROCESS_TEXT ? FontRenderingCompat.preprocessText(text) : text;
    }

    @ModifyVariable(method = "getStringWidth", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private String hodgepodge$preprocessGetStringWidth(String text) {
        return FontRenderingCompat.HAS_PREPROCESS_TEXT ? FontRenderingCompat.preprocessText(text) : text;
    }

    @ModifyVariable(method = "listFormattedStringToWidth", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private String hodgepodge$preprocessListFormatted(String text) {
        return FontRenderingCompat.HAS_PREPROCESS_TEXT ? FontRenderingCompat.preprocessText(text) : text;
    }

    @ModifyVariable(method = "wrapFormattedStringToWidth", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private String hodgepodge$preprocessWrapFormatted(String text) {
        return FontRenderingCompat.HAS_PREPROCESS_TEXT ? FontRenderingCompat.preprocessText(text) : text;
    }
}
