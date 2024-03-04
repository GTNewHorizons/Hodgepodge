package com.mitchej123.hodgepodge.mixins.late.biomesoplenty;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import biomesoplenty.common.integration.ForestryIntegration;

@Mixin(value = ForestryIntegration.class, remap = false)
public class MixinForestryIntegration {

    /**
     * @author glowredman
     * @reason Deduplicate Fermenter and Squeezer recipes and BOP flowers
     * @see <a
     *      href=https://github.com/GTNewHorizons/GT-New-Horizons-Modpack/issues/2886>GTNewHorizons/GT-New-Horizons-Modpack#2886</a>
     */
    @Overwrite
    private static void addFermenterRecipes() {
        /* NO-OP */
    }

    /**
     * @author glowredman
     * @reason Deduplicate Squeezer recipes
     * @see <a
     *      href=https://github.com/GTNewHorizons/GT-New-Horizons-Modpack/issues/2886>GTNewHorizons/GT-New-Horizons-Modpack#2886</a>
     */
    @Overwrite
    private static void addSqueezerRecipes() {
        /* NO-OP */
    }

    /**
     * @author glowredman
     * @reason BOP flowers are already registered by Forestry
     * @see <a
     *      href=https://github.com/GTNewHorizons/GT-New-Horizons-Modpack/issues/2886>GTNewHorizons/GT-New-Horizons-Modpack#2886</a>
     */
    @Overwrite
    private static void addBOPFlowers() {
        /* NO-OP */
    }
}
