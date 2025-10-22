package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldProviderHell;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import com.mitchej123.hodgepodge.config.TweaksConfig;

@Mixin(value = WorldProvider.class, remap = false)
public class MixinWorldProvider {

    @Overwrite
    public double getMovementFactor() {
        if ((Object) this instanceof WorldProviderHell) {
            return TweaksConfig.netherPortalRatio;
        }
        return 1.0;
    }
}
