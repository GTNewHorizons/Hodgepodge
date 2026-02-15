package com.mitchej123.hodgepodge.mixins.late.biomesoplenty;

import java.util.Random;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.mitchej123.hodgepodge.mixins.interfaces.ReusableRandom;

import biomesoplenty.api.biome.BOPBiomeDecorator;
import biomesoplenty.common.utils.RandomForcedPositiveOwned;

@Mixin(value = BOPBiomeDecorator.class, remap = false)
public class MixinBOPBiomeDecorator_CacheRandom {

    @Unique
    private RandomForcedPositiveOwned hodgepodge$cachedRandom;

    @Redirect(
            method = "decorateChunk",
            at = @At(
                    value = "NEW",
                    target = "(Ljava/util/Random;)Lbiomesoplenty/common/utils/RandomForcedPositiveOwned;"))
    private RandomForcedPositiveOwned hodgepodge$reuseCachedRandom(Random parent) {
        if (hodgepodge$cachedRandom == null) {
            hodgepodge$cachedRandom = new RandomForcedPositiveOwned(parent);
        } else {
            ((ReusableRandom) hodgepodge$cachedRandom).hodgepodge$updateParent(parent);
        }
        return hodgepodge$cachedRandom;
    }
}
