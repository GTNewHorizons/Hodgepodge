package com.mitchej123.hodgepodge.core.fml.hooks.mc;

/**
 * All methods assume that selector is greater than 0 and less than the number of non-selector arguments
 */
@SuppressWarnings("unused")
public class RandomAid {

    // Yes, I don't really need this one. But adding this is easier than excluding the 1-arg case,
    // which is still valid bytecode.
    public static int random(int selector, int a) {
        return a;
    }

    public static int random(int selector, int a, int b) {
        return selector % 2 == 1 ? b : a;
    }

    public static int random(int selector, int a, int b, int c) {
        return switch (selector % 3) {
            case 2 -> c;
            case 1 -> b;
            default -> a;
        };
    }

    public static int random(int selector, int a, int b, int c, int d) {
        return switch (selector % 4) {
            case 3 -> d;
            case 2 -> c;
            case 1 -> b;
            default -> a;
        };
    }

    public static int random(int selector, int a, int b, int c, int d, int e) {
        return switch (selector % 5) {
            case 4 -> e;
            case 3 -> d;
            case 2 -> c;
            case 1 -> b;
            default -> a;
        };
    }
}
