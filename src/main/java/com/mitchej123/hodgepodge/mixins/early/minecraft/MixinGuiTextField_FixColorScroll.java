package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiTextField;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.gtnewhorizon.gtnhlib.util.font.FontRendering;

/**
 * Fixes color codes being lost when the chat input field scrolls or splits text at the cursor.
 *
 * Two-part fix: 1. HEAD/RETURN: swap text with preprocessed version so trimStringToWidth correctly treats
 * §x§R§R§G§G§B§B as zero-width format pairs. After recomputing lineScrollOffset, snap it to format code boundaries so
 * substring never starts mid-§x sequence. 2. ModifyArg on drawStringWithShadow: prepend the active format from the
 * scrolled-past / cursor-preceding portion, so colors survive substring clipping.
 */
@Mixin(GuiTextField.class)
public abstract class MixinGuiTextField_FixColorScroll extends Gui {

    @Shadow
    private String text;
    @Shadow
    private int lineScrollOffset;
    @Shadow
    private int cursorPosition;
    @Shadow
    private int selectionEnd;
    @Shadow
    private FontRenderer field_146211_a;

    @Shadow
    public abstract void setSelectionPos(int p_146199_1_);

    @Unique
    private String hodgepodge$originalText;
    @Unique
    private int hodgepodge$originalScrollOffset;
    @Unique
    private int hodgepodge$originalCursorPosition;
    @Unique
    private int hodgepodge$originalSelectionEnd;
    @Unique
    private boolean hodgepodge$swapped;

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

    @Inject(method = "drawTextBox", at = @At("HEAD"))
    private void hodgepodge$preprocessOnDrawHead(CallbackInfo ci) {
        hodgepodge$swapped = false;
        String preprocessed = hodgepodge$safePreprocess(this.text);
        if (preprocessed.length() == this.text.length()) {
            return;
        }

        hodgepodge$originalText = this.text;
        hodgepodge$originalScrollOffset = this.lineScrollOffset;
        hodgepodge$originalCursorPosition = this.cursorPosition;
        hodgepodge$originalSelectionEnd = this.selectionEnd;
        hodgepodge$swapped = true;

        int[] posMap = hodgepodge$buildPositionMap(this.text, preprocessed);

        int mappedSelection = hodgepodge$mapPosition(posMap, hodgepodge$originalSelectionEnd);
        this.text = preprocessed;
        this.cursorPosition = hodgepodge$mapPosition(posMap, hodgepodge$originalCursorPosition);

        // Compute scroll offset in two passes:
        // 1) Let setSelectionPos compute an initial offset
        // 2) Snap past any format code boundary
        // 3) Re-run setSelectionPos so it readjusts to keep cursor visible
        this.lineScrollOffset = 0;
        this.setSelectionPos(mappedSelection);
        int preSnap = this.lineScrollOffset;
        this.lineScrollOffset = hodgepodge$snapToFormatBoundary(preprocessed, this.lineScrollOffset);
        int snapped = this.lineScrollOffset - preSnap; // how many extra chars we skipped
        this.setSelectionPos(mappedSelection);
        // The snap skipped zero-width format chars, but setSelectionPos doesn't know that.
        // Add the skipped count so the cursor stays fully in view.
        if (snapped > 0) {
            this.lineScrollOffset += snapped;
        }
    }

    @Inject(method = "drawTextBox", at = @At("RETURN"))
    private void hodgepodge$restoreAfterDraw(CallbackInfo ci) {
        if (!hodgepodge$swapped) return;
        this.text = hodgepodge$originalText;
        this.lineScrollOffset = hodgepodge$originalScrollOffset;
        this.cursorPosition = hodgepodge$originalCursorPosition;
        this.selectionEnd = hodgepodge$originalSelectionEnd;
    }

    /**
     * First drawStringWithShadow (text before cursor / all visible text): prepend active format from text before scroll
     * offset, and expand any gradient into per-char §x colors so it matches the full gradient's distribution.
     */
    @ModifyArg(
            method = "drawTextBox",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/FontRenderer;drawStringWithShadow(Ljava/lang/String;III)I",
                    ordinal = 0),
            index = 0)
    private String hodgepodge$fixScrolledColorCodes(String beforeCursor) {
        // Check for gradient embedded in the visible before-cursor text
        int gradInText = beforeCursor.lastIndexOf("\u00a7g");
        if (gradInText != -1 && gradInText + 30 <= beforeCursor.length()) {
            return hodgepodge$expandGradientToPerChar(beforeCursor, gradInText);
        }

        if (this.lineScrollOffset <= 0) {
            return beforeCursor;
        }
        String prefix = this.text.substring(0, Math.min(this.lineScrollOffset, this.text.length()));
        String activeFormat = FontRenderer.getFormatFromString(prefix);
        if (activeFormat.isEmpty()) {
            return beforeCursor;
        }
        if (activeFormat.length() >= 30 && activeFormat.charAt(0) == '\u00a7' && activeFormat.charAt(1) == 'g') {
            // Gradient scrolled past: expand continuation as per-char colors
            return hodgepodge$expandScrolledGradientToPerChar(activeFormat, beforeCursor);
        }
        return activeFormat + beforeCursor;
    }

    /**
     * Second drawStringWithShadow (text after cursor): prepend active format from text before cursor position.
     */
    @ModifyArg(
            method = "drawTextBox",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/FontRenderer;drawStringWithShadow(Ljava/lang/String;III)I",
                    ordinal = 1),
            index = 0)
    private String hodgepodge$fixCursorSplitColorCodes(String afterCursor) {
        String prefix = this.text.substring(0, Math.min(this.cursorPosition, this.text.length()));
        String activeFormat = FontRenderer.getFormatFromString(prefix);
        if (activeFormat.isEmpty()) {
            return afterCursor;
        }
        if (activeFormat.length() >= 30 && activeFormat.charAt(0) == '\u00a7' && activeFormat.charAt(1) == 'g') {
            // Expand to per-char §x colors (same path as before-cursor) to avoid
            // float rounding differences between per-char and §g gradient rendering
            return hodgepodge$expandAfterCursorGradientToPerChar(activeFormat, afterCursor);
        }
        return activeFormat + afterCursor;
    }

    /**
     * Expand a gradient embedded in beforeCursor into per-char §x color codes. Uses the full text's gradient parameters
     * so each char gets the exact color it would have in the unsplit gradient.
     */
    @Unique
    private String hodgepodge$expandGradientToPerChar(String beforeCursor, int gradIdx) {
        if (gradIdx + 30 > beforeCursor.length()) return beforeCursor;

        // Parse from full text to get the real gradient spec
        String upToCursor = this.text.substring(0, Math.min(this.cursorPosition, this.text.length()));
        int fullGradIdx = upToCursor.lastIndexOf("\u00a7g");
        if (fullGradIdx == -1 || fullGradIdx + 30 > this.text.length()) return beforeCursor;

        int startRgb = hodgepodge$parseRgb(this.text, fullGradIdx + 2);
        int endRgb = hodgepodge$parseRgb(this.text, fullGradIdx + 16);
        if (startRgb == -1 || endRgb == -1) return beforeCursor;

        int specEnd = fullGradIdx + 30;
        int totalVisible = hodgepodge$countVisibleInGradient(this.text, specEnd);
        if (totalVisible <= 1) return beforeCursor;

        // Count gradient chars scrolled past (if any)
        int scrolledChars = 0;
        if (this.lineScrollOffset > specEnd) {
            scrolledChars = hodgepodge$countVisibleBounded(this.text, specEnd, this.lineScrollOffset);
        }

        return hodgepodge$buildPerCharGradient(beforeCursor, gradIdx, startRgb, endRgb, totalVisible, scrolledChars);
    }

    /**
     * Expand a scrolled-past gradient into per-char §x color codes for the visible text.
     */
    @Unique
    private String hodgepodge$expandScrolledGradientToPerChar(String activeFormat, String beforeCursor) {
        int startRgb = hodgepodge$parseRgb(activeFormat, 2);
        int endRgb = hodgepodge$parseRgb(activeFormat, 16);
        if (startRgb == -1 || endRgb == -1) return activeFormat + beforeCursor;

        String upToCursor = this.text.substring(0, Math.min(this.cursorPosition, this.text.length()));
        int fullGradIdx = upToCursor.lastIndexOf("\u00a7g");
        if (fullGradIdx == -1 || fullGradIdx + 30 > this.text.length()) return activeFormat + beforeCursor;

        int specEnd = fullGradIdx + 30;
        int totalVisible = hodgepodge$countVisibleInGradient(this.text, specEnd);
        if (totalVisible <= 1) return activeFormat + beforeCursor;

        int scrolledChars = hodgepodge$countVisibleBounded(this.text, specEnd, this.lineScrollOffset);

        // Preserve effects/styles from activeFormat (after the 30-char gradient spec)
        String extras = activeFormat.length() > 30 ? activeFormat.substring(30) : "";

        // Build per-char colored text (no embedded gradient spec — it was scrolled past)
        StringBuilder sb = new StringBuilder(beforeCursor.length() * 8);
        if (!extras.isEmpty()) sb.append(extras);
        int gradCharIdx = scrolledChars;
        for (int i = 0; i < beforeCursor.length(); i++) {
            char ch = beforeCursor.charAt(i);
            if (ch == '\u00a7' && i + 1 < beforeCursor.length()) {
                char code = Character.toLowerCase(beforeCursor.charAt(i + 1));
                if (code == 'r' || (code >= '0' && code <= '9')
                        || (code >= 'a' && code <= 'f')
                        || code == 'x'
                        || code == 'y'
                        || code == 'g') {
                    sb.append(beforeCursor, i, beforeCursor.length());
                    return sb.toString();
                }
                sb.append(ch).append(beforeCursor.charAt(i + 1));
                i++;
            } else {
                float t = (float) gradCharIdx / (totalVisible - 1);
                t = Math.min(t, 1f);
                sb.append(hodgepodge$buildSectionX(hodgepodge$lerpRgb(startRgb, endRgb, t)));
                sb.append(ch);
                gradCharIdx++;
            }
        }
        return sb.toString();
    }

    /**
     * Build a string with per-char §x colors replacing an embedded gradient spec. Text before the gradient and
     * non-gradient format codes are preserved.
     */
    @Unique
    private static String hodgepodge$buildPerCharGradient(String text, int gradIdx, int startRgb, int endRgb,
            int totalVisible, int startCharIdx) {
        StringBuilder sb = new StringBuilder(text.length() * 8);
        // Copy everything before the gradient spec
        sb.append(text, 0, gradIdx);

        // Skip the 30-char gradient spec, emit per-char colors for visible chars after it
        int gradCharIdx = startCharIdx;
        for (int i = gradIdx + 30; i < text.length(); i++) {
            char ch = text.charAt(i);
            if (ch == '\u00a7' && i + 1 < text.length()) {
                char code = Character.toLowerCase(text.charAt(i + 1));
                // Gradient terminator: copy rest as-is
                if (code == 'r' || (code >= '0' && code <= '9')
                        || (code >= 'a' && code <= 'f')
                        || code == 'x'
                        || code == 'y'
                        || code == 'g') {
                    sb.append(text, i, text.length());
                    return sb.toString();
                }
                // Non-terminating format code (§l, §o, §w, §j etc.): copy through
                sb.append(ch).append(text.charAt(i + 1));
                i++;
            } else {
                // Visible char: emit its exact gradient color
                float t = (float) gradCharIdx / (totalVisible - 1);
                t = Math.min(t, 1f);
                sb.append(hodgepodge$buildSectionX(hodgepodge$lerpRgb(startRgb, endRgb, t)));
                sb.append(ch);
                gradCharIdx++;
            }
        }
        return sb.toString();
    }

    /**
     * Expand the after-cursor gradient into per-char §x colors using the full gradient's parameters, matching the
     * before-cursor computation path exactly.
     */
    @Unique
    private String hodgepodge$expandAfterCursorGradientToPerChar(String activeFormat, String afterCursor) {
        int startRgb = hodgepodge$parseRgb(activeFormat, 2);
        int endRgb = hodgepodge$parseRgb(activeFormat, 16);
        if (startRgb == -1 || endRgb == -1) return activeFormat + afterCursor;

        String upToCursor = this.text.substring(0, Math.min(this.cursorPosition, this.text.length()));
        int fullGradIdx = upToCursor.lastIndexOf("\u00a7g");
        if (fullGradIdx == -1 || fullGradIdx + 30 > this.text.length()) return activeFormat + afterCursor;

        int specEnd = fullGradIdx + 30;
        int totalVisible = hodgepodge$countVisibleInGradient(this.text, specEnd);
        if (totalVisible <= 1) return activeFormat + afterCursor;

        int visibleBefore = hodgepodge$countVisibleBounded(this.text, specEnd, this.cursorPosition);

        // Preserve effects/styles from activeFormat (after the 30-char gradient spec)
        String extras = activeFormat.length() > 30 ? activeFormat.substring(30) : "";

        StringBuilder sb = new StringBuilder(afterCursor.length() * 8);
        if (!extras.isEmpty()) sb.append(extras);
        int gradCharIdx = visibleBefore;
        for (int i = 0; i < afterCursor.length(); i++) {
            char ch = afterCursor.charAt(i);
            if (ch == '\u00a7' && i + 1 < afterCursor.length()) {
                char code = Character.toLowerCase(afterCursor.charAt(i + 1));
                if (code == 'r' || (code >= '0' && code <= '9')
                        || (code >= 'a' && code <= 'f')
                        || code == 'x'
                        || code == 'y'
                        || code == 'g') {
                    sb.append(afterCursor, i, afterCursor.length());
                    return sb.toString();
                }
                sb.append(ch).append(afterCursor.charAt(i + 1));
                i++;
            } else {
                float t = (float) gradCharIdx / (totalVisible - 1);
                t = Math.min(t, 1f);
                sb.append(hodgepodge$buildSectionX(hodgepodge$lerpRgb(startRgb, endRgb, t)));
                sb.append(ch);
                gradCharIdx++;
            }
        }
        return sb.toString();
    }

    /**
     * If offset lands inside a §x§R§R§G§G§B§B (14-char) or §c (2-char) sequence, advance it past the end so substring
     * never starts mid-format-code.
     */
    @Unique
    private static int hodgepodge$snapToFormatBoundary(String text, int offset) {
        if (offset <= 0 || offset >= text.length()) return offset;

        // Look back up to 13 chars to see if we're inside a §x§R§R§G§G§B§B sequence
        for (int lb = 1; lb <= 13 && offset - lb >= 0; lb++) {
            int candidate = offset - lb;
            if (candidate + 1 < text.length() && text.charAt(candidate) == '\u00a7'
                    && Character.toLowerCase(text.charAt(candidate + 1)) == 'x') {
                // Found §x start. The full §x§R§R§G§G§B§B sequence is 14 chars.
                int seqEnd = candidate + 14;
                if (offset < seqEnd && seqEnd <= text.length()) {
                    return seqEnd;
                }
                break; // Past this §x, no need to look further
            }
        }

        // Check if we're between § and its pair char (simple 2-char format like §c)
        if (text.charAt(offset - 1) == '\u00a7') {
            return Math.min(offset + 1, text.length());
        }

        return offset;
    }

    @Unique
    private static int[] hodgepodge$buildPositionMap(String raw, String preprocessed) {
        int[] map = new int[raw.length() + 1];
        int ri = 0;
        int pi = 0;

        while (ri < raw.length() && pi < preprocessed.length()) {
            map[ri] = pi;

            if (raw.charAt(ri) == '&' && preprocessed.charAt(pi) == '\u00a7') {
                if (pi + 1 < preprocessed.length() && preprocessed.charAt(pi + 1) == 'x') {
                    int rawEnd = Math.min(ri + 7, raw.length());
                    for (int sub = 1; sub < rawEnd - ri; sub++) {
                        map[ri + sub] = pi + sub * 2;
                    }
                    ri += 7;
                    pi += 14;
                } else {
                    if (ri + 1 < raw.length()) {
                        map[ri + 1] = pi + 1;
                    }
                    ri += 2;
                    pi += 2;
                }
                continue;
            }

            ri++;
            pi++;
        }

        map[raw.length()] = preprocessed.length();
        return map;
    }

    @Unique
    private static int hodgepodge$mapPosition(int[] posMap, int rawPos) {
        if (rawPos <= 0) return 0;
        if (rawPos >= posMap.length) return posMap[posMap.length - 1];
        return posMap[rawPos];
    }

    /** Count visible chars from startIdx, stopping at gradient-terminating codes (§r, §0-f, §x, §y, §g). */
    @Unique
    private static int hodgepodge$countVisibleInGradient(String text, int startIdx) {
        int count = 0;
        for (int i = startIdx; i < text.length(); i++) {
            if (text.charAt(i) == '\u00a7' && i + 1 < text.length()) {
                char code = Character.toLowerCase(text.charAt(i + 1));
                if (code == 'r' || (code >= '0' && code <= '9')
                        || (code >= 'a' && code <= 'f')
                        || code == 'x'
                        || code == 'y'
                        || code == 'g') {
                    break;
                }
                i++; // skip non-terminating format codes (k-o, w, j)
            } else {
                count++;
            }
        }
        return count;
    }

    /** Count visible chars from startIdx to endIdx, stopping at gradient-terminating codes. */
    @Unique
    private static int hodgepodge$countVisibleBounded(String text, int startIdx, int endIdx) {
        int count = 0;
        int limit = Math.min(endIdx, text.length());
        for (int i = startIdx; i < limit; i++) {
            if (text.charAt(i) == '\u00a7' && i + 1 < limit) {
                char code = Character.toLowerCase(text.charAt(i + 1));
                if (code == 'r' || (code >= '0' && code <= '9')
                        || (code >= 'a' && code <= 'f')
                        || code == 'x'
                        || code == 'y'
                        || code == 'g') {
                    break;
                }
                i++;
            } else {
                count++;
            }
        }
        return count;
    }

    /** Parse RGB int from §x§R§R§G§G§B§B starting at offset. Returns -1 on failure. */
    @Unique
    private static int hodgepodge$parseRgb(String text, int offset) {
        if (offset + 13 >= text.length()) return -1;
        int val = 0;
        for (int i = 0; i < 6; i++) {
            int d = Character.digit(text.charAt(offset + 3 + i * 2), 16);
            if (d == -1) return -1;
            val = (val << 4) | d;
        }
        return val;
    }

    @Unique
    private static int hodgepodge$lerpRgb(int from, int to, float t) {
        int r = (int) (((from >> 16) & 0xFF) * (1 - t) + ((to >> 16) & 0xFF) * t);
        int g = (int) (((from >> 8) & 0xFF) * (1 - t) + ((to >> 8) & 0xFF) * t);
        int b = (int) ((from & 0xFF) * (1 - t) + (to & 0xFF) * t);
        return (r << 16) | (g << 8) | b;
    }

    @Unique
    private static String hodgepodge$buildSectionX(int rgb) {
        char S = '\u00a7';
        int r = (rgb >> 16) & 0xFF, g = (rgb >> 8) & 0xFF, b = rgb & 0xFF;
        return new StringBuilder(14).append(S).append('x').append(S).append(Character.forDigit((r >> 4) & 0xF, 16))
                .append(S).append(Character.forDigit(r & 0xF, 16)).append(S)
                .append(Character.forDigit((g >> 4) & 0xF, 16)).append(S).append(Character.forDigit(g & 0xF, 16))
                .append(S).append(Character.forDigit((b >> 4) & 0xF, 16)).append(S)
                .append(Character.forDigit(b & 0xF, 16)).toString();
    }
}
