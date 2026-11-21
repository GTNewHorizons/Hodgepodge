package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mitchej123.hodgepodge.util.InfinitePotionDuration;

// Applied after MixinEntityRenderer_NightVisionFade, which overwrites getNightVisionBrightness.
@Mixin(value = EntityRenderer.class, priority = 1500)
public class MixinEntityRenderer_InfiniteDuration {

    @Inject(method = "getNightVisionBrightness", at = @At("HEAD"), cancellable = true)
    private void hodgepodge$fullBrightnessWhenInfinite(EntityPlayer player, float partialTicks,
            CallbackInfoReturnable<Float> cir) {
        if (player.getActivePotionEffect(Potion.nightVision).getDuration() == InfinitePotionDuration.INFINITE) {
            cir.setReturnValue(1.0F);
        }
    }

    @ModifyExpressionValue(
            method = "updateFogColor",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/potion/PotionEffect;getDuration()I"))
    private int hodgepodge$infiniteBlindnessFogColor(int duration) {
        return InfinitePotionDuration.effective(duration);
    }

    @ModifyExpressionValue(
            method = "setupFog",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/potion/PotionEffect;getDuration()I"))
    private int hodgepodge$infiniteBlindnessFog(int duration) {
        return InfinitePotionDuration.effective(duration);
    }
}
