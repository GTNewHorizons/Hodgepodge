package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.client.gui.FontRenderer;

import org.apache.commons.lang3.StringUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@SuppressWarnings("unused")
@Mixin(FontRenderer.class)
public abstract class MixinFontRenderer {

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
    public String wrapFormattedStringToWidth(String str, int wrapWidth) {
        // Always have at least one character per line
        final int firstLineWidth = Math.max(1, this.sizeStringToWidth(str, wrapWidth));
        if (str.length() <= firstLineWidth) {
            return str;
        }
        StringBuilder output = new StringBuilder(str.length() + str.length() / firstLineWidth);
        for (;;) {
            final int lineWidth = Math.max(1, this.sizeStringToWidth(str, wrapWidth));
            final String line = StringUtils.substring(str, 0, lineWidth);
            output.append(line);
            if (lineWidth >= str.length()) {
                break;
            }
            output.append('\n');
            final char nextChar = str.charAt(lineWidth);
            final boolean nextIsBlank = nextChar == ' ' || nextChar == '\n';
            str = getFormatFromString(line) + StringUtils.substring(str, lineWidth + (nextIsBlank ? 1 : 0));
        }
        return output.toString();
    }
}
