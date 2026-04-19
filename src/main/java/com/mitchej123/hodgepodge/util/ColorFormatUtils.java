package com.mitchej123.hodgepodge.util;

/** Shared helpers for parsing, building, and interpolating {@code §x§R§R§G§G§B§B} RGB color sequences. */
public final class ColorFormatUtils {

    private ColorFormatUtils() {}

    /**
     * Parse an RGB int from a {@code §x§R§R§G§G§B§B} sequence starting at {@code offset} (the position of the leading
     * {@code §}). Returns {@code -1} if the sequence is truncated or contains non-hex digits.
     */
    public static int parseRgbFromSectionX(String text, int offset) {
        if (offset + 13 >= text.length()) return -1;
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
        return new StringBuilder(14).append(S).append('x').append(S).append(Character.forDigit((r >> 4) & 0xF, 16))
                .append(S).append(Character.forDigit(r & 0xF, 16)).append(S)
                .append(Character.forDigit((g >> 4) & 0xF, 16)).append(S).append(Character.forDigit(g & 0xF, 16))
                .append(S).append(Character.forDigit((b >> 4) & 0xF, 16)).append(S)
                .append(Character.forDigit(b & 0xF, 16)).toString();
    }

    /** Linearly interpolate between two RGB ints at parameter {@code t} in [0, 1]. */
    public static int lerpRgb(int from, int to, float t) {
        int r = (int) (((from >> 16) & 0xFF) * (1 - t) + ((to >> 16) & 0xFF) * t);
        int g = (int) (((from >> 8) & 0xFF) * (1 - t) + ((to >> 8) & 0xFF) * t);
        int b = (int) ((from & 0xFF) * (1 - t) + (to & 0xFF) * t);
        return (r << 16) | (g << 8) | b;
    }
}
