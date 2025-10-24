package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.world.WorldProviderHell;

import org.spongepowered.asm.mixin.Mixin;

import com.mitchej123.hodgepodge.config.TweaksConfig;

@Mixin(value = WorldProviderHell.class, remap = false)
public class MixinWorldProviderHell {

    public double getMovementFactor() {
        return TweaksConfig.netherPortalRatio;
    }
}
