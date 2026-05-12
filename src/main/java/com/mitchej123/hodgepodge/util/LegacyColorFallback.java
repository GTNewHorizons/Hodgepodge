package com.mitchej123.hodgepodge.util;

import com.gtnewhorizon.gtnhlib.util.font.FontRendering;

/**
 * Fallback text preprocessor for when Angelica is not installed. Converts extended color codes to their nearest vanilla
 * equivalents so text renders with approximate colors instead of garbage.
 */
public class LegacyColorFallback implements FontRendering.TextPreprocessor {

    private static final int[] VANILLA_COLORS = { 0x000000, // §0
            0x0000AA, // §1
            0x00AA00, // §2
            0x00AAAA, // §3
            0xAA0000, // §4
            0xAA00AA, // §5
            0xFFAA00, // §6
            0xAAAAAA, // §7
            0x555555, // §8
            0x5555FF, // §9
            0x55FF55, // §a
            0x55FFFF, // §b
            0xFF5555, // §c
            0xFF55FF, // §d
            0xFFFF55, // §e
            0xFFFFFF, // §f
    };

    private static final char[] VANILLA_CODES = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd',
            'e', 'f' };

    @Override
    public String apply(String text) {
        if (text == null || text.length() < 2) return text;
        StringBuilder sb = new StringBuilder(text.length());
        int len = text.length();

        for (int i = 0; i < len; i++) {
            char c = text.charAt(i);

            if (c == ColorFormatUtils.SECTION && i + 1 < len) {
                char next = Character.toLowerCase(text.charAt(i + 1));

                if (next == 'x' && i + ColorFormatUtils.SECTION_X_SEQ_LEN <= len) {
                    int rgb = parseHexPairs(text, i + 2, 6);
                    if (rgb != -1) {
                        sb.append(ColorFormatUtils.SECTION).append(nearestVanilla(rgb));
                        i += ColorFormatUtils.SECTION_X_SEQ_LEN - 1;
                        continue;
                    }
                } else if (next == 'g' && i + ColorFormatUtils.GRADIENT_SEQ_LEN <= len) {
                    int startRgb = parseSectionX(text, i + 2);
                    if (startRgb != -1) {
                        sb.append(ColorFormatUtils.SECTION).append(nearestVanilla(startRgb));
                        i += ColorFormatUtils.GRADIENT_SEQ_LEN - 1;
                        continue;
                    }
                } else if (next == 'q' || next == 'z' || next == 'v') {
                    i += 1;
                    continue;
                }
            } else if (c == '&' && i + 1 < len) {
                char next = Character.toLowerCase(text.charAt(i + 1));

                if (next == '#' && i + ColorFormatUtils.AMP_HEX_LEN <= len) {
                    int rgb = parseHex6(text, i + 2);
                    if (rgb != -1) {
                        sb.append(ColorFormatUtils.SECTION).append(nearestVanilla(rgb));
                        i += ColorFormatUtils.AMP_HEX_LEN - 1;
                        continue;
                    }
                } else if (next == 'g' && i + ColorFormatUtils.AMP_GRADIENT_LEN <= len
                        && text.charAt(i + 2) == '&'
                        && text.charAt(i + 3) == '#') {
                            int rgb = parseHex6(text, i + 4);
                            if (rgb != -1) {
                                sb.append(ColorFormatUtils.SECTION).append(nearestVanilla(rgb));
                                i += ColorFormatUtils.AMP_GRADIENT_LEN - 1;
                                continue;
                            }
                        } else
                    if (next == 'q' || next == 'z' || next == 'v') {
                        i += 1;
                        continue;
                    } else if (isVanillaCode(next)) {
                        sb.append(ColorFormatUtils.SECTION).append(next);
                        i += 1;
                        continue;
                    }
            }

            sb.append(c);
        }
        return sb.toString();
    }

    @Override
    public boolean handlesAmpCodes() {
        return true;
    }

    private static char nearestVanilla(int rgb) {
        int r = (rgb >> 16) & 0xFF;
        int g = (rgb >> 8) & 0xFF;
        int b = rgb & 0xFF;
        int bestIdx = 0;
        int bestDist = Integer.MAX_VALUE;
        for (int i = 0; i < VANILLA_COLORS.length; i++) {
            int vr = (VANILLA_COLORS[i] >> 16) & 0xFF;
            int vg = (VANILLA_COLORS[i] >> 8) & 0xFF;
            int vb = VANILLA_COLORS[i] & 0xFF;
            int dr = r - vr;
            int dg = g - vg;
            int db = b - vb;
            int dist = dr * dr + dg * dg + db * db;
            if (dist < bestDist) {
                bestDist = dist;
                bestIdx = i;
            }
        }
        return VANILLA_CODES[bestIdx];
    }

    private static int parseSectionX(String str, int start) {
        if (start + ColorFormatUtils.SECTION_X_SEQ_LEN > str.length()) return -1;
        if (str.charAt(start) != ColorFormatUtils.SECTION || Character.toLowerCase(str.charAt(start + 1)) != 'x')
            return -1;
        return parseHexPairs(str, start + 2, 6);
    }

    private static int parseHexPairs(String str, int start, int count) {
        if (start + count * 2 > str.length()) return -1;
        int result = 0;
        for (int k = 0; k < count; k++) {
            int pos = start + k * 2;
            if (str.charAt(pos) != ColorFormatUtils.SECTION) return -1;
            int hex = hexValue(str.charAt(pos + 1));
            if (hex == -1) return -1;
            result = (result << 4) | hex;
        }
        return result;
    }

    private static int parseHex6(String str, int start) {
        if (start + 6 > str.length()) return -1;
        int result = 0;
        for (int k = 0; k < 6; k++) {
            int hex = hexValue(str.charAt(start + k));
            if (hex == -1) return -1;
            result = (result << 4) | hex;
        }
        return result;
    }

    private static int hexValue(char c) {
        if (c >= '0' && c <= '9') return c - '0';
        if (c >= 'a' && c <= 'f') return c - 'a' + 10;
        if (c >= 'A' && c <= 'F') return c - 'A' + 10;
        return -1;
    }

    private static boolean isVanillaCode(char c) {
        return (c >= '0' && c <= '9') || (c >= 'a' && c <= 'f') || (c >= 'k' && c <= 'o') || c == 'r';
    }
}
