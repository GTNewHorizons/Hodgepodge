package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.client.particle.EffectRenderer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import com.mitchej123.hodgepodge.config.TweaksConfig;

@Mixin(EffectRenderer.class)
public class MixinEffectRenderer {

    @ModifyConstant(
            method = "addEffect(Lnet/minecraft/client/particle/EntityFX;)V",
            constant = @Constant(intValue = 4000, ordinal = 0),
            require = 1)
    private int hodgepodge$getParticleLimit(int constant) {
        return TweaksConfig.particleLimit;
    }
}
