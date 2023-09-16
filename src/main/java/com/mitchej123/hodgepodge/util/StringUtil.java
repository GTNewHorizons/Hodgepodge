package com.mitchej123.hodgepodge.util;

// Methods stolen from FontRenderer
public class StringUtil {

    /**
     * Checks if the char code is a hexadecimal character, used to set colour.
     */
    public static boolean isFormatColor(char colorChar) {
        return colorChar >= 48 && colorChar <= 57 || colorChar >= 97 && colorChar <= 102
                || colorChar >= 65 && colorChar <= 70;
    }

    /**
     * Checks if the char code is O-K...lLrRk-o... used to set special formatting.
     */
    public static boolean isFormatSpecial(char formatChar) {
        return formatChar >= 107 && formatChar <= 111 || formatChar >= 75 && formatChar <= 79
                || formatChar == 114
                || formatChar == 82;
    }

    /**
     * Digests a string for nonprinting formatting characters then returns a string containing only that formatting.
     */
    public static String getFormatFromString(String p_78282_0_) {
        String s1 = "";
        int i = -1;
        int j = p_78282_0_.length();
        while ((i = p_78282_0_.indexOf(167, i + 1)) != -1) {
            if (i < j - 1) {
                char c0 = p_78282_0_.charAt(i + 1);
                if (isFormatColor(c0)) {
                    s1 = "\u00a7" + c0;
                } else if (isFormatSpecial(c0)) {
                    s1 = s1 + "\u00a7" + c0;
                }
            }
        }
        return s1;
    }

}
