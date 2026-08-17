package com.mitchej123.hodgepodge.util;

import java.util.HashMap;
import java.util.Map;
import java.util.function.IntFunction;

public final class PotionMetadataCache<T> {

    public static final int ALL_METADATA_BITS = 0x7FFF;
    private static final int SPLASH_BIT = 1 << 14;

    private final int metadataMask;
    private final Map<Integer, T> values = new HashMap<>();

    public PotionMetadataCache(int metadataMask) {
        this.metadataMask = metadataMask;
    }

    public T get(int metadata, IntFunction<T> loader) {
        int key = metadata & metadataMask;
        if (!values.containsKey(key)) {
            values.put(key, loader.apply(metadata));
        }
        return values.get(key);
    }

    public static int findRelevantBits(Iterable<?> requirements, Iterable<?> amplifiers) {
        int result = addExpressionBits(SPLASH_BIT, requirements);
        return result == ALL_METADATA_BITS ? result : addExpressionBits(result, amplifiers);
    }

    private static int addExpressionBits(int bits, Iterable<?> expressions) {
        for (Object value : expressions) {
            if (!(value instanceof String expression)) return ALL_METADATA_BITS;

            for (int i = 0; i < expression.length(); i++) {
                char current = expression.charAt(i);
                if (current == '=' || current == '<' || current == '>') return ALL_METADATA_BITS;
                if (isAsciiDigit(current)) {
                    int numberStart = i;
                    int number = 0;
                    do {
                        number = number * 10 + expression.charAt(i) - '0';
                        i++;
                    } while (i < expression.length() && isAsciiDigit(expression.charAt(i)));
                    i--;

                    int previous = numberStart - 1;
                    while (previous >= 0 && Character.isWhitespace(expression.charAt(previous))) previous--;
                    if (previous < 0 || expression.charAt(previous) != '*') {
                        int shiftedBit = number & 31;
                        if (shiftedBit < 15) bits |= 1 << shiftedBit;
                    }
                } else if (!Character.isWhitespace(current) && "|&!*-+".indexOf(current) < 0) {
                    return ALL_METADATA_BITS;
                }
            }
        }
        return bits;
    }

    private static boolean isAsciiDigit(char character) {
        return character >= '0' && character <= '9';
    }
}
