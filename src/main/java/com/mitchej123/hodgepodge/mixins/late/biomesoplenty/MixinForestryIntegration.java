package com.mitchej123.hodgepodge.mixins.late.biomesoplenty;

import biomesoplenty.common.integration.ForestryIntegration;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(ForestryIntegration.class)
public class MixinForestryIntegration {

    /**
     * @author glowredman
     * @reason Deduplicate Fermenter recipes
     * @see <a href=https://github.com/GTNewHorizons/GT-New-Horizons-Modpack/issues/2886>GTNewHorizons/GT-New-Horizons-Modpack#2886</a>
     */
    @Overwrite(remap = false)
    private static void addFermenterRecipes() {
        /* NO-OP */
    }

    /**
     * @author glowredman
     * @reason Deduplicate Squeezer recipes
     * @see <a href=https://github.com/GTNewHorizons/GT-New-Horizons-Modpack/issues/2886>GTNewHorizons/GT-New-Horizons-Modpack#2886</a>
     */
    @Overwrite(remap = false)
    private static void addSqueezerRecipes() {
        /* NO-OP */
    }

    /**
     * @author glowredman
     * @reason BOP flowers are already registered by Forestry
     * @see <a href=https://github.com/GTNewHorizons/GT-New-Horizons-Modpack/issues/2886>GTNewHorizons/GT-New-Horizons-Modpack#2886</a>
     */
    @Overwrite(remap = false)
    private static void addBOPFlowers() {
        /* NO-OP */
    }
}
