package com.mitchej123.hodgepodge.util;

/** Shared helpers for parsing, building, and interpolating {@code §x§R§R§G§G§B§B} RGB color sequences. */
public final class ColorFormatUtils {

    private ColorFormatUtils() {}

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
        char S = '\u00a7';
        int r = (rgb >> 16) & 0xFF, g = (rgb >> 8) & 0xFF, b = rgb & 0xFF;
        return new StringBuilder(SECTION_X_SEQ_LEN).append(S).append('x').append(S)
                .append(Character.forDigit((r >> 4) & 0xF, 16)).append(S).append(Character.forDigit(r & 0xF, 16))
                .append(S).append(Character.forDigit((g >> 4) & 0xF, 16)).append(S)
                .append(Character.forDigit(g & 0xF, 16)).append(S).append(Character.forDigit((b >> 4) & 0xF, 16))
                .append(S).append(Character.forDigit(b & 0xF, 16)).toString();
    }

    /** Linearly interpolate between two RGB ints at parameter {@code t} in [0, 1]. */
    public static int lerpRgb(int from, int to, float t) {
        int r = (int) (((from >> 16) & 0xFF) * (1 - t) + ((to >> 16) & 0xFF) * t);
        int g = (int) (((from >> 8) & 0xFF) * (1 - t) + ((to >> 8) & 0xFF) * t);
        int b = (int) ((from & 0xFF) * (1 - t) + (to & 0xFF) * t);
        return (r << 16) | (g << 8) | b;
    }
}
