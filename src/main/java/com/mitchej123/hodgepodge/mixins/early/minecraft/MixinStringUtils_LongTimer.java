package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.util.StringUtils;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(StringUtils.class)
public class MixinStringUtils_LongTimer {

    /**
     * @author Eldrinn-Elantey
     * @reason Vanilla only formats mm:ss, so an hour long potion effect reads as 60:00. Show h:mm:ss past the hour.
     */
    @Overwrite
    public static String ticksToElapsedTime(int ticks) {
        int seconds = ticks / 20;
        final int minutes = seconds / 60;
        seconds %= 60;

        if (minutes < 60) {
            return String.format("%d:%02d", minutes, seconds);
        }

        return String.format("%d:%02d:%02d", minutes / 60, minutes % 60, seconds);
    }
}
