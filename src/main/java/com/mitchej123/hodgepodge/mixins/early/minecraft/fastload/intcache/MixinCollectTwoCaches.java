package com.mitchej123.hodgepodge.mixins.early.minecraft.fastload.intcache;

import static com.mitchej123.hodgepodge.hax.FastIntCache.releaseCache;

import net.minecraft.world.gen.layer.GenLayerHills;
import net.minecraft.world.gen.layer.GenLayerRiverMix;
import net.minecraft.world.gen.layer.GenLayerVoronoiZoom;
import net.minecraft.world.gen.layer.GenLayerZoom;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.sugar.Local;

@Mixin({ GenLayerHills.class, GenLayerRiverMix.class, GenLayerVoronoiZoom.class, GenLayerZoom.class })
public class MixinCollectTwoCaches {

    @Inject(method = "getInts", at = @At(value = "RETURN"))
    private void hodgepodge$collectInts(int areaX, int areaY, int areaWidth, int areaHeight,
            CallbackInfoReturnable<int[]> cir, @Local(ordinal = 0) int[] ints, @Local(ordinal = 1) int[] ints2) {
        releaseCache(ints);
        releaseCache(ints2);
    }
}
