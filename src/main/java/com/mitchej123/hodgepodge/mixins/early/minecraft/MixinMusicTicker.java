package com.mitchej123.hodgepodge.mixins.early.minecraft;

import java.util.Random;

import net.minecraft.client.audio.MusicTicker;
import net.minecraft.util.MathHelper;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.mitchej123.hodgepodge.config.TweaksConfig;

@Mixin(MusicTicker.class)
public class MixinMusicTicker {

    @Shadow
    @Final
    private Random field_147679_a;

    @ModifyExpressionValue(
            method = "update",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/MathHelper;getRandomIntegerInRange(Ljava/util/Random;II)I",
                    ordinal = 1))
    private int hodgepodge$configurableMusicDelay(int original, @Local MusicTicker.MusicType musictype) {
        // Only override long-form in-game music; leave menu/credits/end_boss vanilla.
        switch (musictype) {
            case GAME, CREATIVE, NETHER, END -> {
                int min = TweaksConfig.musicDelayMinSeconds * 20;
                int max = Math.max(min, TweaksConfig.musicDelayMaxSeconds * 20);
                return MathHelper.getRandomIntegerInRange(this.field_147679_a, min, max);
            }
            default -> {
                return original;
            }
        }
    }
}
