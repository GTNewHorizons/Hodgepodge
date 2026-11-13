package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.client.entity.EntityPlayerSP;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mitchej123.hodgepodge.util.InfinitePotionDuration;

@Mixin(EntityPlayerSP.class)
public class MixinEntityPlayerSP_InfiniteNausea {

    @ModifyExpressionValue(
            method = "onLivingUpdate",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/potion/PotionEffect;getDuration()I"))
    private int hodgepodge$infiniteNausea(int duration) {
        return InfinitePotionDuration.effective(duration);
    }
}
