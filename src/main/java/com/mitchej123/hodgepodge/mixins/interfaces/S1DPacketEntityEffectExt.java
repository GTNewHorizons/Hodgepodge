package com.mitchej123.hodgepodge.mixins.interfaces;

import com.mitchej123.hodgepodge.net.MessagePotionDuration;

public interface S1DPacketEntityEffectExt {

    /** The message correcting the truncated duration, or null if the duration fits in a short. */
    MessagePotionDuration hodgepodge$getDurationMessage();
}
