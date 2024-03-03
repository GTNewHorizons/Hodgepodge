package com.mitchej123.hodgepodge.mixins.late.biomesoplenty;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import biomesoplenty.common.integration.ForestryIntegration;

@Mixin(ForestryIntegration.class)
public class MixinForestryIntegration {

    /**
     * @author glowredman
     * @reason Deduplicate Fermenter and Squeezer recipes and BOP flowers
     * @see <a
     *      href=https://github.com/GTNewHorizons/GT-New-Horizons-Modpack/issues/2886>GTNewHorizons/GT-New-Horizons-Modpack#2886</a>
     */
    @Inject(
            at = @At("HEAD"),
            cancellable = true,
            method = { "addFermenterRecipes", "addSqueezerRecipes", "addBOPFlowers" },
            remap = false)
    private void hodgepodge$exit(CallbackInfo ci) {
        ci.cancel();
    }
}
