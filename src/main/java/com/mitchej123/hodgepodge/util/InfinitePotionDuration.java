package com.mitchej123.hodgepodge.util;

/** Duration marker for an infinite potion effect, like in newer Minecraft versions. */
public final class InfinitePotionDuration {

    public static final int INFINITE = -1;

    /** The infinity sign, built from its code point so the source does not depend on its encoding. */
    public static final String SYMBOL = String.valueOf((char) 0x221E);

    private InfinitePotionDuration() {}

    /** Maps the infinite marker to a duration longer than any timer vanilla compares against. */
    public static int effective(int duration) {
        return duration == INFINITE ? Integer.MAX_VALUE : duration;
    }
}
