package com.mitchej123.hodgepodge.mixins.early.minecraft;

import java.util.Random;

import net.minecraft.client.audio.MusicTicker;
import net.minecraft.util.MathHelper;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.mitchej123.hodgepodge.config.TweaksConfig;

@Mixin(MusicTicker.class)
public class MixinMusicTicker {

    @Redirect(
            method = "update",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/MathHelper;getRandomIntegerInRange(Ljava/util/Random;II)I"))
    private int hodgepodge$configurableMusicDelay(Random rand, int min, int max) {
        // leave menu/credits/end_boss alone
        if (min >= 1200) {
            int cfgMin = TweaksConfig.musicDelayMinSeconds * 20;
            int cfgMax = Math.max(cfgMin, TweaksConfig.musicDelayMaxSeconds * 20);
            return MathHelper.getRandomIntegerInRange(rand, cfgMin, cfgMax);
        }
        return MathHelper.getRandomIntegerInRange(rand, min, max);
    }
}
