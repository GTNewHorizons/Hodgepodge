package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.world.biome.BiomeGenBase;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BiomeGenBase.class)
public abstract class MixinBiomeGenBase {

    @Unique
    private static final float ABSOLUTE_ZERO = -459.67f / 100f; // in Fahrenheit scale

    @Inject(method = "getFloatTemperature", at = @At("RETURN"), cancellable = true)
    public final void getFloatTemperature(int p_150564_1_, int p_150564_2_, int p_150564_3_,
            CallbackInfoReturnable<Float> cir) {
        cir.setReturnValue(Math.max(cir.getReturnValue(), ABSOLUTE_ZERO));
    }
}
