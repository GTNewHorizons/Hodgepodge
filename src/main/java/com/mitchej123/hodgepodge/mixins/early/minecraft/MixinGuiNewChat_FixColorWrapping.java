package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiNewChat;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import com.llamalad7.mixinextras.sugar.Local;
import com.mitchej123.hodgepodge.util.ColorFormatUtils;
import com.mitchej123.hodgepodge.util.FontRenderingCompat;

@Mixin(GuiNewChat.class)
public class MixinGuiNewChat_FixColorWrapping {

    @Unique
    private static String hodgepodge$safePreprocess(String s) {
        return FontRenderingCompat.HAS_PREPROCESS_TEXT ? FontRenderingCompat.preprocessText(s) : s;
    }

    /**
     * Preprocess the per-component string (convert &#RRGGBB → §x§R§R§G§G§B§B) before any width calculations, trimming,
     * or format extraction happen.
     */
    @ModifyVariable(
            method = "func_146237_a",
            at = @At(
                    value = "INVOKE_ASSIGN",
                    target = "Lnet/minecraft/client/gui/GuiNewChat;func_146235_b(Ljava/lang/String;)Ljava/lang/String;"),
            name = "s")
    private String hodgepodge$preprocessBeforeWrap(String s) {
        s = hodgepodge$safePreprocess(s);
        return hodgepodge$expandGradients(s);
    }

    /**
     * Expand §g gradients into per-character §x colors so that line wrapping preserves the correct color at every
     * character. Without this, the renderer's per-line countVisibleChars reaches t=1.0 at the end of each wrapped line,
     * causing a color jump at wrap boundaries.
     */
    @Unique
    private static String hodgepodge$expandGradients(String text) {
        int gIdx = text.indexOf("\u00a7g");
        if (gIdx == -1) return text;

        StringBuilder sb = new StringBuilder(text.length() + 128);
        int last = 0;

        while (gIdx != -1 && gIdx + ColorFormatUtils.GRADIENT_SEQ_LEN - 1 < text.length()) {
            // Validate §g followed by two §x§R§R§G§G§B§B sequences
            final int firstRgbPos = gIdx + ColorFormatUtils.GRADIENT_FIRST_RGB_OFFSET;
            final int secondRgbPos = gIdx + ColorFormatUtils.GRADIENT_SECOND_RGB_OFFSET;
            if (text.charAt(firstRgbPos) != ColorFormatUtils.SECTION
                    || Character.toLowerCase(text.charAt(firstRgbPos + 1)) != 'x'
                    || text.charAt(secondRgbPos) != ColorFormatUtils.SECTION
                    || Character.toLowerCase(text.charAt(secondRgbPos + 1)) != 'x') {
                gIdx = text.indexOf("\u00a7g", gIdx + 2);
                continue;
            }

            int startRgb = ColorFormatUtils.parseRgbFromSectionX(text, firstRgbPos);
            int endRgb = ColorFormatUtils.parseRgbFromSectionX(text, secondRgbPos);
            if (startRgb == -1 || endRgb == -1) {
                gIdx = text.indexOf("\u00a7g", gIdx + 2);
                continue;
            }

            // Count visible chars in the gradient span (until §r, color code, or another gradient/rainbow)
            int textStart = gIdx + ColorFormatUtils.GRADIENT_SEQ_LEN;
            int totalVisible = hodgepodge$countGradientVisible(text, textStart);
            if (totalVisible <= 0) {
                gIdx = text.indexOf("\u00a7g", gIdx + 2);
                continue;
            }

            // Append everything before the §g prefix
            sb.append(text, last, gIdx);

            // Expand each visible char with its interpolated §x color
            int visIdx = 0;
            for (int i = textStart; i < text.length(); i++) {
                char ch = text.charAt(i);
                if (ch == ColorFormatUtils.SECTION && i + 1 < text.length()) {
                    char code = Character.toLowerCase(text.charAt(i + 1));
                    if (ColorFormatUtils.isGradientTerminator(code)) {
                        last = i;
                        break;
                    }
                    // Non-terminating format code (style toggles): pass through
                    sb.append(ch).append(text.charAt(i + 1));
                    i++;
                } else {
                    // Visible char: emit interpolated §x color + the char
                    float t = totalVisible > 1 ? (float) visIdx / (totalVisible - 1) : 0f;
                    t = Math.min(t, 1f);
                    int rgb = ColorFormatUtils.lerpRgb(startRgb, endRgb, t);
                    sb.append(ColorFormatUtils.buildSectionX(rgb));
                    sb.append(ch);
                    visIdx++;
                    if (visIdx >= totalVisible) {
                        last = i + 1;
                        break;
                    }
                }
                last = i + 1;
            }

            gIdx = text.indexOf("\u00a7g", last);
        }

        if (last == 0) return text;
        sb.append(text, last, text.length());
        return sb.toString();
    }

    /** Count visible chars from startIdx until a gradient-terminating code (§r, §0-f, §x, §q, §g). */
    @Unique
    private static int hodgepodge$countGradientVisible(String text, int startIdx) {
        int count = 0;
        for (int i = startIdx; i < text.length(); i++) {
            char ch = text.charAt(i);
            if (ch == ColorFormatUtils.SECTION && i + 1 < text.length()) {
                char code = Character.toLowerCase(text.charAt(i + 1));
                if (ColorFormatUtils.isGradientTerminator(code)) {
                    break;
                }
                i++; // skip non-terminating format codes
            } else {
                count++;
            }
        }
        return count;
    }

    @ModifyVariable(
            method = "func_146237_a",
            at = @At(
                    value = "NEW",
                    target = "(Ljava/lang/String;)Lnet/minecraft/util/ChatComponentText;",
                    ordinal = 2,
                    shift = At.Shift.BEFORE),
            name = "s2")
    private String hodgepodge$fixColorWrapping(String s2, @Local(name = "s1") String s1) {
        String format = FontRenderer.getFormatFromString(s1);
        return format + s2;
    }

}
