package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiNewChat;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import com.gtnewhorizon.gtnhlib.util.font.FontRendering;
import com.llamalad7.mixinextras.sugar.Local;

@Mixin(GuiNewChat.class)
public class MixinGuiNewChat_FixColorWrapping {

    @Unique
    private static boolean hodgepodge$preprocessChecked = false;
    @Unique
    private static boolean hodgepodge$preprocessAvailable = false;

    @Unique
    private static String hodgepodge$safePreprocess(String s) {
        if (!hodgepodge$preprocessChecked) {
            hodgepodge$preprocessChecked = true;
            try {
                FontRendering.class.getMethod("preprocessText", String.class);
                hodgepodge$preprocessAvailable = true;
            } catch (NoSuchMethodException e) {
                hodgepodge$preprocessAvailable = false;
            }
        }
        return hodgepodge$preprocessAvailable ? FontRendering.preprocessText(s) : s;
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
        return hodgepodge$safePreprocess(s);
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

        // Handle gradient: instead of restarting the full gradient on each wrapped line,
        // compute the interpolated color at the wrap point and emit a new gradient
        // from that color to the end color. This makes the gradient continue seamlessly.
        if (format.length() >= 30 && format.charAt(0) == '\u00a7' && format.charAt(1) == 'g') {
            return hodgepodge$continueGradient(format, s1, s2);
        }

        return format + s2;
    }

    /**
     * Build a continuation gradient from the interpolated color at the wrap point to the original end color.
     */
    @Unique
    private static String hodgepodge$continueGradient(String gradientFormat, String s1, String s2) {
        // Parse start/end RGB from §g§x§R§R§G§G§B§B§x§R§R§G§G§B§B (30 chars)
        int startRgb = hodgepodge$parseRgbFromSectionX(gradientFormat, 2);
        int endRgb = hodgepodge$parseRgbFromSectionX(gradientFormat, 16);
        if (startRgb == -1 || endRgb == -1) {
            return gradientFormat + s2;
        }

        // Count visible chars rendered on the current line (after gradient prefix in s1)
        int gradientStart = s1.indexOf("\u00a7g");
        int visibleOnLine = gradientStart >= 0 ? hodgepodge$countVisible(s1, gradientStart + 30)
                : hodgepodge$countVisible(s1, 0);

        // Count visible chars remaining
        int visibleRemaining = hodgepodge$countVisible(s2, 0);
        int totalVisible = visibleOnLine + visibleRemaining;

        if (totalVisible <= 1) {
            // Not enough chars for meaningful gradient, use end color
            return hodgepodge$buildSectionX(endRgb) + s2;
        }

        // Interpolate color at the wrap point
        float t = (float) visibleOnLine / (totalVisible - 1);
        t = Math.min(t, 1f);
        int interpRgb = hodgepodge$lerpRgb(startRgb, endRgb, t);

        // Emit a new gradient from interpolated color to end color
        return "\u00a7g" + hodgepodge$buildSectionX(interpRgb) + hodgepodge$buildSectionX(endRgb) + s2;
    }

    @Unique
    private static int hodgepodge$lerpRgb(int from, int to, float t) {
        int r = (int) (((from >> 16) & 0xFF) * (1 - t) + ((to >> 16) & 0xFF) * t);
        int g = (int) (((from >> 8) & 0xFF) * (1 - t) + ((to >> 8) & 0xFF) * t);
        int b = (int) ((from & 0xFF) * (1 - t) + (to & 0xFF) * t);
        return (r << 16) | (g << 8) | b;
    }

    /** Parse RGB int from §x§R§R§G§G§B§B starting at offset in text. Returns -1 on failure. */
    @Unique
    private static int hodgepodge$parseRgbFromSectionX(String text, int offset) {
        if (offset + 13 >= text.length()) return -1;
        // §x at offset, then §R§R§G§G§B§B = 6 hex digit pairs at offset+2,+4,+6,+8,+10,+12
        int val = 0;
        for (int i = 0; i < 6; i++) {
            int d = Character.digit(text.charAt(offset + 3 + i * 2), 16);
            if (d == -1) return -1;
            val = (val << 4) | d;
        }
        return val;
    }

    /** Build §x§R§R§G§G§B§B from an RGB int. */
    @Unique
    private static String hodgepodge$buildSectionX(int rgb) {
        int r = (rgb >> 16) & 0xFF;
        int g = (rgb >> 8) & 0xFF;
        int b = rgb & 0xFF;
        char S = '\u00a7';
        return new StringBuilder(14).append(S).append('x').append(S).append(Character.forDigit((r >> 4) & 0xF, 16))
                .append(S).append(Character.forDigit(r & 0xF, 16)).append(S)
                .append(Character.forDigit((g >> 4) & 0xF, 16)).append(S).append(Character.forDigit(g & 0xF, 16))
                .append(S).append(Character.forDigit((b >> 4) & 0xF, 16)).append(S)
                .append(Character.forDigit(b & 0xF, 16)).toString();
    }

    /** Count visible characters in text starting from startIdx, skipping §X format pairs. */
    @Unique
    private static int hodgepodge$countVisible(String text, int startIdx) {
        int count = 0;
        for (int i = startIdx; i < text.length(); i++) {
            if (text.charAt(i) == '\u00a7' && i + 1 < text.length()) {
                i++;
            } else {
                count++;
            }
        }
        return count;
    }
}
