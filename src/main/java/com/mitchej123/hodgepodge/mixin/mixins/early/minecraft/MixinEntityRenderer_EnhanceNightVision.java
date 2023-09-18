package com.mitchej123.hodgepodge.mixin.mixins.early.minecraft;

import net.minecraft.client.renderer.EntityRenderer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

@Mixin(EntityRenderer.class)
public class MixinEntityRenderer_EnhanceNightVision {

    @ModifyExpressionValue(
            method = "updateFogColor",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/EntityLivingBase;isPotionActive(Lnet/minecraft/potion/Potion;)Z",
                    ordinal = 1))
    private boolean hodgepodge$enhanceNightVision(boolean b) {
        return false;
    }

}
