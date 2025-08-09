package com.mitchej123.hodgepodge.util;

public class StringUtil {

    /**
     * A faster version of {@link net.minecraft.util.EnumChatFormatting#getTextWithoutFormattingCodes(String)}
     */
    public static String removeFormattingCodes(String text) {
        if (text == null || text.length() < 2) return text;
        final int len = text.length();
        final char[] chars = text.toCharArray();
        int count = 0;
        for (int i = 0; i < len; i++) {
            final char c = chars[i];
            if (c == '§' && i + 1 < len && "0123456789abcdefklmnorABCDEFKLMNOR".indexOf(chars[i + 1]) != -1) {
                i++;
                continue;
            }
            chars[count++] = c;
        }
        return new String(chars, 0, count);
    }
}
