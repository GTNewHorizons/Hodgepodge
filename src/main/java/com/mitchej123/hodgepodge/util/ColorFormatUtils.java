package com.mitchej123.hodgepodge.util;

/** Shared helpers for parsing, building, and interpolating {@code §x§R§R§G§G§B§B} RGB color sequences. */
public final class ColorFormatUtils {

    private ColorFormatUtils() {}

    public static final char SECTION = '§';

    /** Length of a {@code §x§R§R§G§G§B§B} RGB sequence. */
    public static final int SECTION_X_SEQ_LEN = 14;

    /** Offset of the first RGB sequence inside a {@code §g§x...§x...} gradient spec (past the {@code §g}). */
    public static final int GRADIENT_FIRST_RGB_OFFSET = 2;

    /** Offset of the second RGB sequence inside a {@code §g§x...§x...} gradient spec. */
    public static final int GRADIENT_SECOND_RGB_OFFSET = GRADIENT_FIRST_RGB_OFFSET + SECTION_X_SEQ_LEN;

    /** Length of a full {@code §g§x§R§R§G§G§B§B§x§R§R§G§G§B§B} gradient spec. */
    public static final int GRADIENT_SEQ_LEN = GRADIENT_SECOND_RGB_OFFSET + SECTION_X_SEQ_LEN;

    /** Length of a raw {@code &#RRGGBB} ampersand-RGB sequence. */
    public static final int AMP_HEX_LEN = 8;

    /** Length of a raw {@code &g&#RRGGBB&#RRGGBB} ampersand-gradient sequence. */
    public static final int AMP_GRADIENT_LEN = 2 + AMP_HEX_LEN + AMP_HEX_LEN;

    /** Valid single {@code &} codes (excludes {@code g} which only converts as {@code &g&#RRGGBB&#RRGGBB}). */
    public static final String VALID_AMP_SINGLE_CODES = "0123456789abcdefklmnorqzv";

    /**
     * Codes that terminate an active gradient: reset ({@code r}), legacy colors ({@code 0}-{@code 9}, {@code a}-{@code
     * f}), RGB ({@code x}), rainbow ({@code q}), new gradient ({@code g}). Style codes ({@code k}-{@code o}) and other
     * effect codes ({@code z} wave, {@code v} dinnerbone) do not terminate — they don't change per-char color.
     */
    private static final String GRADIENT_TERMINATOR_CODES = "r0123456789abcdefxqg";

    private static final boolean[] IS_GRADIENT_TERMINATOR = new boolean[128];

    static {
        for (int i = 0; i < GRADIENT_TERMINATOR_CODES.length(); i++) {
            IS_GRADIENT_TERMINATOR[GRADIENT_TERMINATOR_CODES.charAt(i)] = true;
        }
    }

    /**
     * @param lowerCode the format code character (already lowercased) appearing immediately after a {@code §} or
     *                  {@code &} marker
     * @return {@code true} if this code terminates an active gradient
     */
    public static boolean isGradientTerminator(char lowerCode) {
        return lowerCode < 128 && IS_GRADIENT_TERMINATOR[lowerCode];
    }

    /**
     * Parse an RGB int from a {@code §x§R§R§G§G§B§B} sequence starting at {@code offset} (the position of the leading
     * {@code §}). Returns {@code -1} if the sequence is truncated or contains non-hex digits.
     */
    public static int parseRgbFromSectionX(String text, int offset) {
        if (offset + SECTION_X_SEQ_LEN - 1 >= text.length()) return -1;
        int val = 0;
        for (int i = 0; i < 6; i++) {
            int d = Character.digit(text.charAt(offset + 3 + i * 2), 16);
            if (d == -1) return -1;
            val = (val << 4) | d;
        }
        return val;
    }

    /** Build a {@code §x§R§R§G§G§B§B} sequence from an RGB int. */
    public static String buildSectionX(int rgb) {
        int r = (rgb >> 16) & 0xFF, g = (rgb >> 8) & 0xFF, b = rgb & 0xFF;
        return new StringBuilder(SECTION_X_SEQ_LEN).append(SECTION).append('x').append(SECTION)
                .append(Character.forDigit((r >> 4) & 0xF, 16)).append(SECTION).append(Character.forDigit(r & 0xF, 16))
                .append(SECTION).append(Character.forDigit((g >> 4) & 0xF, 16)).append(SECTION)
                .append(Character.forDigit(g & 0xF, 16)).append(SECTION).append(Character.forDigit((b >> 4) & 0xF, 16))
                .append(SECTION).append(Character.forDigit(b & 0xF, 16)).toString();
    }

    /** Check if all characters from {@code from} to {@code to} (inclusive) are hex digits. */
    public static boolean allHex(String text, int from, int to) {
        for (int j = from; j <= to; j++) {
            if (Character.digit(text.charAt(j), 16) == -1) return false;
        }
        return true;
    }

    /** Check if a valid {@code &#RRGGBB} (8-char) ampersand-hex code starts at {@code idx}. */
    public static boolean isAmpHexAt(String text, int idx) {
        return idx + AMP_HEX_LEN <= text.length() && text.charAt(idx + 1) == '#' && allHex(text, idx + 2, idx + 7);
    }

    /** Check if a valid {@code &g&#RRGGBB&#RRGGBB} (18-char) ampersand-gradient starts at {@code idx}. */
    public static boolean isAmpGradientAt(String text, int idx) {
        return idx + AMP_GRADIENT_LEN <= text.length() && text.charAt(idx + 2) == '&'
                && text.charAt(idx + 3) == '#'
                && text.charAt(idx + 10) == '&'
                && text.charAt(idx + 11) == '#'
                && allHex(text, idx + 4, idx + 9)
                && allHex(text, idx + 12, idx + 17);
    }

    /** Linearly interpolate between two RGB ints at parameter {@code t} in [0, 1]. */
    public static int lerpRgb(int from, int to, float t) {
        int r = (int) (((from >> 16) & 0xFF) * (1 - t) + ((to >> 16) & 0xFF) * t);
        int g = (int) (((from >> 8) & 0xFF) * (1 - t) + ((to >> 8) & 0xFF) * t);
        int b = (int) ((from & 0xFF) * (1 - t) + (to & 0xFF) * t);
        return (r << 16) | (g << 8) | b;
    }

    /** Convert a hue (0-360) at full saturation/value to an RGB int. */
    public static int hsvToRgb(float hue) {
        int h = (int) (hue / 60f) % 6;
        float f = hue / 60f - h;
        float r, g, b;
        switch (h) {
            case 0:
                r = 1;
                g = f;
                b = 0;
                break;
            case 1:
                r = 1 - f;
                g = 1;
                b = 0;
                break;
            case 2:
                r = 0;
                g = 1;
                b = f;
                break;
            case 3:
                r = 0;
                g = 1 - f;
                b = 1;
                break;
            case 4:
                r = f;
                g = 0;
                b = 1;
                break;
            default:
                r = 1;
                g = 0;
                b = 1 - f;
                break;
        }
        return ((int) (r * 255) << 16) | ((int) (g * 255) << 8) | (int) (b * 255);
    }

    /** Count visible chars from {@code startIdx}, stopping at gradient-terminating codes. */
    public static int countVisibleInGradient(String text, int startIdx) {
        int count = 0;
        for (int i = startIdx; i < text.length(); i++) {
            if (text.charAt(i) == ColorFormatUtils.SECTION && i + 1 < text.length()) {
                char code = Character.toLowerCase(text.charAt(i + 1));
                if (isGradientTerminator(code)) {
                    break;
                }
                i++;
            } else {
                count++;
            }
        }
        return count;
    }

    /** Count visible chars from {@code startIdx} to {@code endIdx}, stopping at gradient-terminating codes. */
    public static int countVisibleBounded(String text, int startIdx, int endIdx) {
        int count = 0;
        int limit = Math.min(endIdx, text.length());
        for (int i = startIdx; i < limit; i++) {
            if (text.charAt(i) == ColorFormatUtils.SECTION && i + 1 < limit) {
                char code = Character.toLowerCase(text.charAt(i + 1));
                if (isGradientTerminator(code)) {
                    break;
                }
                i++;
            } else {
                count++;
            }
        }
        return count;
    }
}
