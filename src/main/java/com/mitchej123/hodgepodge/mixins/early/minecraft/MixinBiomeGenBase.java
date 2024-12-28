package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.world.biome.BiomeGenBase;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;

@Mixin(BiomeGenBase.class)
public abstract class MixinBiomeGenBase {

    @Unique
    private static final float ABSOLUTE_ZERO = -459.67f / 100f; // in Fahrenheit scale

    @ModifyReturnValue(method = "getFloatTemperature", at = @At("RETURN"))
    public final float hodgepodge$getFloatTemperature(float original) {
        return Math.max(original, ABSOLUTE_ZERO);
    }
}
