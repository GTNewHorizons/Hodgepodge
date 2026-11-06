package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// Applied after MixinEntityRenderer_NightVisionFade, which overwrites the same method.
@Mixin(value = EntityRenderer.class, priority = 1500)
public class MixinEntityRenderer_InfiniteNightVision {

    @Inject(method = "getNightVisionBrightness", at = @At("HEAD"), cancellable = true)
    private void hodgepodge$fullBrightnessWhenInfinite(EntityPlayer player, float partialTicks,
            CallbackInfoReturnable<Float> cir) {
        if (player.getActivePotionEffect(Potion.nightVision).getDuration() == -1) {
            cir.setReturnValue(1.0F);
        }
    }
}
