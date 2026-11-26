package com.mitchej123.hodgepodge.util;

import java.util.function.Predicate;

import net.minecraft.potion.PotionEffect;

import com.mitchej123.hodgepodge.mixins.interfaces.PotionEffectHiddenExt;

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

    /** Removes matching effects hidden beneath the given active effect. */
    public static boolean removeHiddenEffects(PotionEffect active, Predicate<PotionEffect> matches) {
        boolean removed = false;
        PotionEffect current = active;
        while (current instanceof PotionEffectHiddenExt chain) {
            PotionEffect hidden = chain.hodgepodge$getHidden();
            if (hidden == null) break;

            if (matches.test(hidden)) {
                chain.hodgepodge$setHidden(((PotionEffectHiddenExt) hidden).hodgepodge$getHidden());
                removed = true;
            } else {
                current = hidden;
            }
        }
        return removed;
    }
}
