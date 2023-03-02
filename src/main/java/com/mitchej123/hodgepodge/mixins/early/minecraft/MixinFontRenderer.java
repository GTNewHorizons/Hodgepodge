package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.ResourceLocation;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(FontRenderer.class)
public abstract class MixinFontRenderer extends FontRenderer {

    // dummy to satisfy javac
    private MixinFontRenderer(GameSettings p_i1035_1_, ResourceLocation p_i1035_2_, TextureManager p_i1035_3_,
            boolean p_i1035_4_) {
        super(p_i1035_1_, p_i1035_2_, p_i1035_3_, p_i1035_4_);
    }

    @Shadow
    protected abstract int sizeStringToWidth(String str, int wrapWidth);

    @Shadow
    protected static String getFormatFromString(String p_78282_0_) {
        throw new UnsupportedOperationException();
    }

    /**
     * Inserts newline and formatting into a string to wrap it within the specified width.
     *
     * @reason The vanilla code uses recursion which leads to unreadable stacktraces, and doesn't sanity-check wrapWidth
     * @author eigenraven
     */
    @Overwrite
    String wrapFormattedStringToWidth(String str, int wrapWidth) {
        // Always have at least one character per line
        final int firstLineWidth = Math.max(1, this.sizeStringToWidth(str, wrapWidth));
        if (str.length() <= firstLineWidth) {
            return str;
        }
        StringBuilder output = new StringBuilder(str.length() + str.length() / firstLineWidth);
        for (;;) {
            final int lineWidth = Math.max(1, this.sizeStringToWidth(str, wrapWidth));
            final String line = str.substring(0, lineWidth);
            output.append(line);
            if (lineWidth >= str.length()) {
                break;
            }
            output.append('\n');
            output.append(getFormatFromString(line));
            final char nextChar = str.charAt(lineWidth);
            final boolean nextIsBlank = nextChar == ' ' || nextChar == '\n';
            str = str.substring(lineWidth + (nextIsBlank ? 1 : 0));
        }
        return output.toString();
    }
}
