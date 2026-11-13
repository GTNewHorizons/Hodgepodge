package com.mitchej123.hodgepodge.mixins.interfaces;

import net.minecraft.potion.PotionEffect;

public interface PotionEffectHiddenExt {

    /** The weaker effect kept under this one, which takes over when this one ends. */
    PotionEffect hodgepodge$getHidden();

    void hodgepodge$setHidden(PotionEffect hidden);
}
