package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.client.gui.FontRenderer;

import org.apache.commons.lang3.StringUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import com.mitchej123.hodgepodge.util.ColorFormatUtils;

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
        StringBuilder formatting = new StringBuilder();
        for (;;) {
            final int lineWidth = Math.max(1, this.sizeStringToWidth(str, wrapWidth));
            final String line = StringUtils.substring(str, 0, lineWidth);
            output.append(line);
            if (lineWidth >= str.length()) {
                break;
            }
            output.append('\n');
            formatting.append(line);
            String newFormat = getFormatFromString(formatting.toString());

            // Handle gradient continuation: interpolate color at wrap point
            if (newFormat.length() >= ColorFormatUtils.GRADIENT_SEQ_LEN
                    && newFormat.charAt(0) == ColorFormatUtils.SECTION
                    && newFormat.charAt(1) == 'g') {
                String remainder = StringUtils.substring(
                        str,
                        lineWidth + (str.charAt(lineWidth) == ' ' || str.charAt(lineWidth) == '\n' ? 1 : 0));
                newFormat = hodgepodge$interpolateGradient(newFormat, formatting.toString(), remainder);
            }

            formatting.setLength(0);
            formatting.append(newFormat);
            output.append(formatting);
            final char nextChar = str.charAt(lineWidth);
            final boolean nextIsBlank = nextChar == ' ' || nextChar == '\n';
            str = StringUtils.substring(str, lineWidth + (nextIsBlank ? 1 : 0));
        }
        return output.toString();
    }

    @Unique
    private static String hodgepodge$interpolateGradient(String gradientFormat, String rendered, String remaining) {
        int startRgb = ColorFormatUtils
                .parseRgbFromSectionX(gradientFormat, ColorFormatUtils.GRADIENT_FIRST_RGB_OFFSET);
        int endRgb = ColorFormatUtils.parseRgbFromSectionX(gradientFormat, ColorFormatUtils.GRADIENT_SECOND_RGB_OFFSET);
        if (startRgb == -1 || endRgb == -1) return gradientFormat;

        int gradientIdx = rendered.indexOf("\u00a7g");
        int visRendered = gradientIdx >= 0
                ? hodgepodge$countVisible(rendered, gradientIdx + ColorFormatUtils.GRADIENT_SEQ_LEN)
                : hodgepodge$countVisible(rendered, 0);
        int visRemaining = hodgepodge$countVisible(remaining, 0);
        int total = visRendered + visRemaining;

        if (total <= 1) {
            return ColorFormatUtils.buildSectionX(endRgb);
        }

        float t = Math.min((float) visRendered / (total - 1), 1f);
        int interpRgb = ColorFormatUtils.lerpRgb(startRgb, endRgb, t);
        return "\u00a7g" + ColorFormatUtils.buildSectionX(interpRgb) + ColorFormatUtils.buildSectionX(endRgb);
    }

    @Unique
    private static int hodgepodge$countVisible(String text, int startIdx) {
        int count = 0;
        for (int i = startIdx; i < text.length(); i++) {
            if (text.charAt(i) == ColorFormatUtils.SECTION && i + 1 < text.length()) {
                i++;
            } else {
                count++;
            }
        }
        return count;
    }
}
