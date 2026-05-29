package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.mitchej123.hodgepodge.util.ColorFormatUtils;
import com.mitchej123.hodgepodge.util.FontRenderingCompat;

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
    private boolean isFocused;
    @Shadow
    private boolean isEnabled;

    @Shadow
    public abstract void setSelectionPos(int p_146199_1_);

    @Shadow
    public abstract void setCursorPosition(int pos);

    @Shadow
    public abstract int getNthWordFromCursor(int n);

    @Shadow
    public abstract int getSelectionEnd();

    @Shadow
    public abstract int getWidth();

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
    private static String hodgepodge$safePreprocess(String s) {
        return FontRenderingCompat.HAS_PREPROCESS_TEXT ? FontRenderingCompat.preprocessText(s) : s;
    }

    @Unique
    private static String hodgepodge$getActiveEffects(String text) {
        boolean wave = false, dinnerbone = false, rainbow = false;
        for (int i = 0; i < text.length() - 1; i++) {
            if (text.charAt(i) == ColorFormatUtils.SECTION) {
                char code = Character.toLowerCase(text.charAt(i + 1));
                if (code == 'r') {
                    wave = false;
                    dinnerbone = false;
                    rainbow = false;
                } else if (code == 'z') {
                    wave = true;
                } else if (code == 'v') {
                    dinnerbone = true;
                } else if (code == 'q') {
                    rainbow = true;
                } else if (ColorFormatUtils.isGradientTerminator(code)) {
                    rainbow = false;
                }
                i++;
            }
        }
        if (!wave && !dinnerbone && !rainbow) return "";
        StringBuilder sb = new StringBuilder(6);
        if (rainbow) sb.append(ColorFormatUtils.SECTION).append('q');
        if (wave) sb.append(ColorFormatUtils.SECTION).append('z');
        if (dinnerbone) sb.append(ColorFormatUtils.SECTION).append('v');
        return sb.toString();
    }

    @Unique
    private static String hodgepodge$stripEffectCodes(String format) {
        if (format.indexOf('q') == -1 && format.indexOf('z') == -1 && format.indexOf('v') == -1) return format;
        StringBuilder sb = new StringBuilder(format.length());
        for (int i = 0; i < format.length() - 1; i++) {
            if (format.charAt(i) == ColorFormatUtils.SECTION) {
                char code = Character.toLowerCase(format.charAt(i + 1));
                if (code != 'q' && code != 'z' && code != 'v') {
                    sb.append(format, i, i + 2);
                }
                i++;
            }
        }
        return sb.toString();
    }

    @Unique
    private static boolean hodgepodge$formatHasBold(String format) {
        for (int i = 0; i < format.length() - 1; i++) {
            if (format.charAt(i) == ColorFormatUtils.SECTION && Character.toLowerCase(format.charAt(i + 1)) == 'l') {
                return true;
            }
        }
        return false;
    }

    @Unique
    private void hodgepodge$compensateBoldScroll(String text) {
        if (this.lineScrollOffset <= 0 || this.lineScrollOffset >= text.length()) return;

        int targetPos = Math.min(this.cursorPosition, text.length());
        String prefix = text.substring(0, this.lineScrollOffset);
        String activeFormat = FontRenderer.getFormatFromString(prefix);
        if (!hodgepodge$formatHasBold(activeFormat)) return;

        int fieldWidth = this.getWidth();

        for (int i = 0; i < 20 && this.lineScrollOffset < targetPos; i++) {
            String withFormat = activeFormat + text.substring(this.lineScrollOffset);
            String trimmed = this.field_146211_a.trimStringToWidth(withFormat, fieldWidth);
            int visibleEnd = this.lineScrollOffset + trimmed.length() - activeFormat.length();

            if (targetPos <= visibleEnd) break;

            this.lineScrollOffset++;
            this.lineScrollOffset = hodgepodge$snapToFormatBoundary(text, this.lineScrollOffset);
            if (this.lineScrollOffset >= text.length()) break;

            prefix = text.substring(0, this.lineScrollOffset);
            activeFormat = FontRenderer.getFormatFromString(prefix);
            if (!hodgepodge$formatHasBold(activeFormat)) break;
        }
    }

    @Unique
    private int hodgepodge$getRainbowCharOffset(int endPos) {
        String fullText = this.text;
        int end = Math.min(endPos, fullText.length());
        int rainbowStart = -1;
        for (int i = 0; i < end - 1; i++) {
            if (fullText.charAt(i) != ColorFormatUtils.SECTION) continue;
            char code = Character.toLowerCase(fullText.charAt(i + 1));
            if (code == 'q') {
                rainbowStart = i + 2;
            } else if (ColorFormatUtils.isGradientTerminator(code)) {
                rainbowStart = -1;
                if (code == 'x' && i + ColorFormatUtils.SECTION_X_SEQ_LEN <= fullText.length()) {
                    i += ColorFormatUtils.SECTION_X_SEQ_LEN - 1;
                    continue;
                } else if (code == 'g' && i + ColorFormatUtils.GRADIENT_SEQ_LEN <= fullText.length()) {
                    i += ColorFormatUtils.GRADIENT_SEQ_LEN - 1;
                    continue;
                }
            }
            i++;
        }
        if (rainbowStart == -1 || rainbowStart >= end) return 0;
        int count = 0;
        for (int i = rainbowStart; i < end; i++) {
            if (fullText.charAt(i) == ColorFormatUtils.SECTION && i + 1 < end) {
                i++;
            } else {
                count++;
            }
        }
        return count;
    }

    @Unique
    private static String hodgepodge$expandRainbowToPerChar(String text, int startCharIdx, String extraPrefix) {
        StringBuilder sb = new StringBuilder(text.length() * 16);
        if (!extraPrefix.isEmpty()) sb.append(extraPrefix);
        int charIdx = startCharIdx;
        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            if (ch == ColorFormatUtils.SECTION && i + 1 < text.length()) {
                char code = Character.toLowerCase(text.charAt(i + 1));
                if (ColorFormatUtils.isGradientTerminator(code)) {
                    sb.append(text, i, text.length());
                    return sb.toString();
                }
                sb.append(ch).append(text.charAt(i + 1));
                i++;
            } else {
                float hue = (charIdx * 15f) % 360f;
                sb.append(ColorFormatUtils.buildSectionX(ColorFormatUtils.hsvToRgb(hue)));
                sb.append(ch);
                charIdx++;
            }
        }
        return sb.toString();
    }

    @Inject(method = "drawTextBox", at = @At("HEAD"))
    private void hodgepodge$preprocessOnDrawHead(CallbackInfo ci) {
        hodgepodge$swapped = false;
        String preprocessed = hodgepodge$safePreprocess(this.text);
        if (preprocessed.equals(this.text)) {
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

        this.lineScrollOffset = 0;
        for (int iter = 0; iter < 10; iter++) {
            int prev = this.lineScrollOffset;
            this.setSelectionPos(mappedSelection);
            this.lineScrollOffset = hodgepodge$snapToFormatBoundary(preprocessed, this.lineScrollOffset);
            if (this.lineScrollOffset == prev) break;
        }
        hodgepodge$compensateBoldScroll(preprocessed);
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
        // Vanilla shadow-pass style leak fix: drawString calls renderStringAtPos twice (shadow + main)
        // but only resets styles once. Style codes set during shadow leak into main pass.
        // Prepending reset at the start fixes this (no-op in shadow, resets leaked styles in main).
        int gradInText = beforeCursor.lastIndexOf("\u00a7g");
        if (gradInText != -1 && gradInText + ColorFormatUtils.GRADIENT_SEQ_LEN <= beforeCursor.length()) {
            return "\u00a7r" + hodgepodge$expandGradientToPerChar(beforeCursor, gradInText);
        }

        if (this.lineScrollOffset <= 0) {
            return "\u00a7r" + beforeCursor;
        }
        String prefix = this.text.substring(0, Math.min(this.lineScrollOffset, this.text.length()));
        String activeFormat = FontRenderer.getFormatFromString(prefix);
        String activeEffects = hodgepodge$getActiveEffects(prefix);
        String colorAndStyles = hodgepodge$stripEffectCodes(activeFormat);
        if (colorAndStyles.isEmpty() && activeEffects.isEmpty()) {
            return "\u00a7r" + beforeCursor;
        }
        if (activeEffects.length() >= 2 && activeEffects.charAt(1) == 'q') {
            int scrolledChars = hodgepodge$getRainbowCharOffset(this.lineScrollOffset);
            String otherEffects = activeEffects.length() > 2 ? activeEffects.substring(2) : "";
            return "\u00a7r"
                    + hodgepodge$expandRainbowToPerChar(beforeCursor, scrolledChars, otherEffects + colorAndStyles);
        }
        if (colorAndStyles.length() >= ColorFormatUtils.GRADIENT_SEQ_LEN
                && colorAndStyles.charAt(0) == ColorFormatUtils.SECTION
                && colorAndStyles.charAt(1) == 'g') {
            String expanded = hodgepodge$expandGradientSegment(colorAndStyles, beforeCursor, this.lineScrollOffset);
            return "\u00a7r" + (activeEffects.isEmpty() ? expanded : activeEffects + expanded);
        }
        return "\u00a7r" + activeEffects + colorAndStyles + beforeCursor;
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
        String activeEffects = hodgepodge$getActiveEffects(prefix);
        String colorAndStyles = hodgepodge$stripEffectCodes(activeFormat);
        if (colorAndStyles.isEmpty() && activeEffects.isEmpty()) {
            return "\u00a7r" + afterCursor;
        }
        if (activeEffects.length() >= 2 && activeEffects.charAt(1) == 'q') {
            int rainbowOffset = hodgepodge$getRainbowCharOffset(this.cursorPosition);
            String otherEffects = activeEffects.length() > 2 ? activeEffects.substring(2) : "";
            return "\u00a7r"
                    + hodgepodge$expandRainbowToPerChar(afterCursor, rainbowOffset, otherEffects + colorAndStyles);
        }
        if (colorAndStyles.length() >= ColorFormatUtils.GRADIENT_SEQ_LEN
                && colorAndStyles.charAt(0) == ColorFormatUtils.SECTION
                && colorAndStyles.charAt(1) == 'g') {
            // Expand to per-char §x colors (same path as before-cursor) to avoid
            // float rounding differences between per-char and §g gradient rendering
            String expanded = hodgepodge$expandGradientSegment(colorAndStyles, afterCursor, this.cursorPosition);
            return "\u00a7r" + (activeEffects.isEmpty() ? expanded : activeEffects + expanded);
        }
        return "\u00a7r" + activeEffects + colorAndStyles + afterCursor;
    }

    /**
     * Expand a gradient embedded in beforeCursor into per-char §x color codes. Uses the full text's gradient parameters
     * so each char gets the exact color it would have in the unsplit gradient.
     */
    @Unique
    private String hodgepodge$expandGradientToPerChar(String beforeCursor, int gradIdx) {
        if (gradIdx + ColorFormatUtils.GRADIENT_SEQ_LEN > beforeCursor.length()) return beforeCursor;

        // Parse from full text to get the real gradient spec
        String upToCursor = this.text.substring(0, Math.min(this.cursorPosition, this.text.length()));
        int fullGradIdx = upToCursor.lastIndexOf("\u00a7g");
        if (fullGradIdx == -1 || fullGradIdx + ColorFormatUtils.GRADIENT_SEQ_LEN > this.text.length())
            return beforeCursor;

        int startRgb = ColorFormatUtils
                .parseRgbFromSectionX(this.text, fullGradIdx + ColorFormatUtils.GRADIENT_FIRST_RGB_OFFSET);
        int endRgb = ColorFormatUtils
                .parseRgbFromSectionX(this.text, fullGradIdx + ColorFormatUtils.GRADIENT_SECOND_RGB_OFFSET);
        if (startRgb == -1 || endRgb == -1) return beforeCursor;

        int specEnd = fullGradIdx + ColorFormatUtils.GRADIENT_SEQ_LEN;
        int totalVisible = ColorFormatUtils.countVisibleInGradient(this.text, specEnd);
        if (totalVisible <= 1) return beforeCursor;

        // Count gradient chars scrolled past (if any)
        int scrolledChars = 0;
        if (this.lineScrollOffset > specEnd) {
            scrolledChars = ColorFormatUtils.countVisibleBounded(this.text, specEnd, this.lineScrollOffset);
        }

        return hodgepodge$buildPerCharGradient(beforeCursor, gradIdx, startRgb, endRgb, totalVisible, scrolledChars);
    }

    @Unique
    private String hodgepodge$expandGradientSegment(String activeFormat, String visibleText, int countFromOffset) {
        int startRgb = ColorFormatUtils.parseRgbFromSectionX(activeFormat, ColorFormatUtils.GRADIENT_FIRST_RGB_OFFSET);
        int endRgb = ColorFormatUtils.parseRgbFromSectionX(activeFormat, ColorFormatUtils.GRADIENT_SECOND_RGB_OFFSET);
        if (startRgb == -1 || endRgb == -1) return activeFormat + visibleText;

        String upToCursor = this.text.substring(0, Math.min(this.cursorPosition, this.text.length()));
        int fullGradIdx = upToCursor.lastIndexOf("\u00a7g");
        if (fullGradIdx == -1 || fullGradIdx + ColorFormatUtils.GRADIENT_SEQ_LEN > this.text.length())
            return activeFormat + visibleText;

        int specEnd = fullGradIdx + ColorFormatUtils.GRADIENT_SEQ_LEN;
        int totalVisible = ColorFormatUtils.countVisibleInGradient(this.text, specEnd);
        if (totalVisible <= 1) return activeFormat + visibleText;

        int startCharIdx = ColorFormatUtils.countVisibleBounded(this.text, specEnd, countFromOffset);

        String extras = activeFormat.length() > ColorFormatUtils.GRADIENT_SEQ_LEN
                ? activeFormat.substring(ColorFormatUtils.GRADIENT_SEQ_LEN)
                : "";

        StringBuilder sb = new StringBuilder(visibleText.length() * 8);
        if (!extras.isEmpty()) sb.append(extras);
        int gradCharIdx = startCharIdx;
        for (int i = 0; i < visibleText.length(); i++) {
            char ch = visibleText.charAt(i);
            if (ch == ColorFormatUtils.SECTION && i + 1 < visibleText.length()) {
                char code = Character.toLowerCase(visibleText.charAt(i + 1));
                if (ColorFormatUtils.isGradientTerminator(code)) {
                    sb.append(visibleText, i, visibleText.length());
                    return sb.toString();
                }
                sb.append(ch).append(visibleText.charAt(i + 1));
                i++;
            } else {
                float t = (float) gradCharIdx / (totalVisible - 1);
                t = Math.min(t, 1f);
                sb.append(ColorFormatUtils.buildSectionX(ColorFormatUtils.lerpRgb(startRgb, endRgb, t)));
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

        // Skip the gradient spec, emit per-char colors for visible chars after it
        int gradCharIdx = startCharIdx;
        for (int i = gradIdx + ColorFormatUtils.GRADIENT_SEQ_LEN; i < text.length(); i++) {
            char ch = text.charAt(i);
            if (ch == ColorFormatUtils.SECTION && i + 1 < text.length()) {
                char code = Character.toLowerCase(text.charAt(i + 1));
                // Gradient terminator: copy rest as-is
                if (ColorFormatUtils.isGradientTerminator(code)) {
                    sb.append(text, i, text.length());
                    return sb.toString();
                }
                // Non-terminating format code (§l, §o, §z, §v etc.): copy through
                sb.append(ch).append(text.charAt(i + 1));
                i++;
            } else {
                // Visible char: emit its exact gradient color
                float t = (float) gradCharIdx / (totalVisible - 1);
                t = Math.min(t, 1f);
                sb.append(ColorFormatUtils.buildSectionX(ColorFormatUtils.lerpRgb(startRgb, endRgb, t)));
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

        // Look back to see if we're inside a §x§R§R§G§G§B§B sequence
        for (int lb = 1; lb < ColorFormatUtils.SECTION_X_SEQ_LEN && offset - lb >= 0; lb++) {
            int candidate = offset - lb;
            if (candidate + 1 < text.length() && text.charAt(candidate) == ColorFormatUtils.SECTION
                    && Character.toLowerCase(text.charAt(candidate + 1)) == 'x') {
                int seqEnd = candidate + ColorFormatUtils.SECTION_X_SEQ_LEN;
                if (offset < seqEnd && seqEnd <= text.length()) {
                    return seqEnd;
                }
                break; // Past this §x, no need to look further
            }
        }

        // Check if we're between § and its pair char (simple 2-char format like §c)
        if (text.charAt(offset - 1) == ColorFormatUtils.SECTION) {
            return Math.min(offset + 1, text.length());
        }

        return offset;
    }

    @Unique
    private static int[] hodgepodge$buildPositionMap(String raw, String preprocessed) {
        int[] map = new int[raw.length() + 1];
        int ri = 0;
        int pi = 0;

        while (ri < raw.length()) {
            map[ri] = pi;

            if (raw.charAt(ri) == '&' && ri + 1 < raw.length()) {
                char next = Character.toLowerCase(raw.charAt(ri + 1));
                int rawLen = 0;
                int prepLen = 0;

                if (next == 'g' && ColorFormatUtils.isAmpGradientAt(raw, ri)) {
                    rawLen = ColorFormatUtils.AMP_GRADIENT_LEN;
                    if (pi + 1 < preprocessed.length() && preprocessed.charAt(pi) == ColorFormatUtils.SECTION
                            && Character.toLowerCase(preprocessed.charAt(pi + 1)) == 'g') {
                        prepLen = ColorFormatUtils.GRADIENT_SEQ_LEN;
                    } else if (pi < preprocessed.length() && preprocessed.charAt(pi) == ColorFormatUtils.SECTION) {
                        prepLen = 2;
                    }
                } else if (next == '#' && ColorFormatUtils.isAmpHexAt(raw, ri)) {
                    rawLen = ColorFormatUtils.AMP_HEX_LEN;
                    if (pi + 1 < preprocessed.length() && preprocessed.charAt(pi) == ColorFormatUtils.SECTION
                            && Character.toLowerCase(preprocessed.charAt(pi + 1)) == 'x') {
                        prepLen = ColorFormatUtils.SECTION_X_SEQ_LEN;
                    } else if (pi < preprocessed.length() && preprocessed.charAt(pi) == ColorFormatUtils.SECTION) {
                        prepLen = 2;
                    }
                } else if (ColorFormatUtils.VALID_AMP_SINGLE_CODES.indexOf(next) != -1) {
                    rawLen = 2;
                    if (next == 'q' || next == 'z' || next == 'v') {
                        if (pi + 1 < preprocessed.length() && preprocessed.charAt(pi) == ColorFormatUtils.SECTION
                                && Character.toLowerCase(preprocessed.charAt(pi + 1)) == next) {
                            prepLen = 2;
                        }
                    } else {
                        prepLen = 2;
                    }
                }

                if (rawLen > 0) {
                    for (int sub = 1; sub < rawLen; sub++) {
                        if (ri + sub < raw.length()) {
                            map[ri + sub] = pi;
                        }
                    }
                    ri += rawLen;
                    pi += prepLen;
                    continue;
                }
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

    // --- Format-code-aware cursor navigation ---

    @Inject(method = "textboxKeyTyped", at = @At("HEAD"), cancellable = true)
    private void hodgepodge$formatAwareCursorNav(char typedChar, int keyCode, CallbackInfoReturnable<Boolean> cir) {
        if (!this.isFocused || !FontRenderingCompat.HAS_PREPROCESS_TEXT) return;
        if (this.text == null || this.text.indexOf('&') == -1) return;

        if (keyCode == 203 || keyCode == 205) { // LEFT or RIGHT
            int dir = keyCode == 205 ? 1 : -1;
            boolean forward = dir > 0;
            int target;
            if (GuiScreen.isShiftKeyDown()) {
                if (GuiScreen.isCtrlKeyDown()) {
                    target = this.getNthWordFromCursor(dir);
                } else {
                    target = this.getSelectionEnd() + dir;
                }
                target = hodgepodge$snapPastAmpCodes(this.text, target, forward);
                this.setSelectionPos(target);
            } else if (GuiScreen.isCtrlKeyDown()) {
                target = this.getNthWordFromCursor(dir);
                target = hodgepodge$snapPastAmpCodes(this.text, target, forward);
                this.setCursorPosition(target);
            } else {
                target = hodgepodge$snapPastAmpCodes(this.text, this.selectionEnd + dir, forward);
                this.setCursorPosition(target);
            }
            cir.setReturnValue(true);
        } else if (this.isEnabled && this.selectionEnd == this.cursorPosition && !GuiScreen.isCtrlKeyDown()) {
            if (keyCode == 14) { // BACKSPACE — delete format code as atomic unit
                int deletePos = this.cursorPosition - 1;
                if (deletePos >= 0) {
                    int[] range = hodgepodge$findAmpCodeRange(this.text, deletePos);
                    if (range != null) {
                        this.text = this.text.substring(0, range[0]) + this.text.substring(range[1]);
                        this.setCursorPosition(range[0]);
                        cir.setReturnValue(true);
                    }
                }
            } else if (keyCode == 211) { // DELETE — delete format code as atomic unit
                if (this.cursorPosition < this.text.length()) {
                    int[] range = hodgepodge$findAmpCodeRange(this.text, this.cursorPosition);
                    if (range != null) {
                        this.text = this.text.substring(0, range[0]) + this.text.substring(range[1]);
                        this.setCursorPosition(range[0]);
                        cir.setReturnValue(true);
                    }
                }
            }
        }
    }

    /**
     * If {@code pos} is inside an {@code &}-based format code, snap it to the boundary: forward → end, backward →
     * start. For forward movement, landing exactly on the {@code &} also snaps past the code.
     */
    @Unique
    private static int hodgepodge$snapPastAmpCodes(String text, int pos, boolean forward) {
        if (text == null || text.isEmpty()) return pos;
        pos = Math.max(0, Math.min(pos, text.length()));

        int i = 0;
        while (i < text.length()) {
            if (text.charAt(i) != '&' || i + 1 >= text.length()) {
                i++;
                continue;
            }
            char next = Character.toLowerCase(text.charAt(i + 1));
            int codeEnd = -1;

            if (next == 'g' && ColorFormatUtils.isAmpGradientAt(text, i)) {
                codeEnd = i + ColorFormatUtils.AMP_GRADIENT_LEN;
            } else if (next == '#' && ColorFormatUtils.isAmpHexAt(text, i)) {
                codeEnd = i + ColorFormatUtils.AMP_HEX_LEN;
            } else if (ColorFormatUtils.VALID_AMP_SINGLE_CODES.indexOf(next) != -1) {
                codeEnd = i + 2;
            }

            if (codeEnd != -1) {
                boolean inside = forward ? (pos >= i && pos < codeEnd) : (pos > i && pos < codeEnd);
                if (inside) {
                    return forward ? codeEnd : i;
                }
                i = codeEnd;
            } else {
                i++;
            }
        }
        return pos;
    }

    /** Returns {@code [start, end)} if {@code pos} is inside an {@code &}-based format code, or {@code null}. */
    @Unique
    private static int[] hodgepodge$findAmpCodeRange(String text, int pos) {
        if (text == null || pos < 0 || pos >= text.length()) return null;
        int i = 0;
        while (i < text.length()) {
            if (text.charAt(i) != '&' || i + 1 >= text.length()) {
                i++;
                continue;
            }
            char next = Character.toLowerCase(text.charAt(i + 1));
            int codeEnd = -1;

            if (next == 'g' && ColorFormatUtils.isAmpGradientAt(text, i)) {
                codeEnd = i + ColorFormatUtils.AMP_GRADIENT_LEN;
            } else if (next == '#' && ColorFormatUtils.isAmpHexAt(text, i)) {
                codeEnd = i + ColorFormatUtils.AMP_HEX_LEN;
            } else if (ColorFormatUtils.VALID_AMP_SINGLE_CODES.indexOf(next) != -1) {
                codeEnd = i + 2;
            }

            if (codeEnd != -1) {
                if (pos >= i && pos < codeEnd) {
                    return new int[] { i, codeEnd };
                }
                i = codeEnd;
            } else {
                i++;
            }
        }
        return null;
    }

}
