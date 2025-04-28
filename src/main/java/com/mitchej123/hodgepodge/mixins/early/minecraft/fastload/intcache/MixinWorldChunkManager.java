package com.mitchej123.hodgepodge.mixins.early.minecraft.fastload.intcache;

import static com.mitchej123.hodgepodge.hax.FastIntCache.releaseCache;

import java.util.List;
import java.util.Random;

import net.minecraft.world.ChunkPosition;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManager;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;

@Mixin(WorldChunkManager.class)
public class MixinWorldChunkManager {

    @Redirect(
            method = { "getRainfall", "getBiomesForGeneration", "areBiomesViable",
                    "getBiomeGenAt([Lnet/minecraft/world/biome/BiomeGenBase;IIIIZ)[Lnet/minecraft/world/biome/BiomeGenBase;",
                    "findBiomePosition" },
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/gen/layer/IntCache;resetIntCache()V"))
    private void hodgepodge$nukeIntCache() {}

    @Inject(
            method = "getRainfall",
            at = @At(
                    value = "INVOKE_ASSIGN",
                    target = "Lnet/minecraft/world/gen/layer/GenLayer;getInts(IIII)[I",
                    shift = At.Shift.AFTER))
    private void hodgepodge$shareInts(float[] p_76936_1_, int p_76936_2_, int p_76936_3_, int p_76936_4_,
            int p_76936_5_, CallbackInfoReturnable<float[]> cir, @Local(name = "aint") int[] ints,
            @Share("cache") LocalRef<int[]> cache) {
        cache.set(ints);
    }

    @Inject(method = "getRainfall", at = @At(value = "RETURN"))
    private void hodgepodge$collectInts(float[] p_76936_1_, int p_76936_2_, int p_76936_3_, int p_76936_4_,
            int p_76936_5_, CallbackInfoReturnable<float[]> cir, @Share("cache") LocalRef<int[]> cache) {
        releaseCache(cache.get());
    }

    @Inject(
            method = "getBiomesForGeneration",
            at = @At(
                    value = "INVOKE_ASSIGN",
                    target = "Lnet/minecraft/world/gen/layer/GenLayer;getInts(IIII)[I",
                    shift = At.Shift.AFTER))
    private void hodgepodge$shareInts(BiomeGenBase[] p_76937_1_, int p_76937_2_, int p_76937_3_, int p_76937_4_,
            int p_76937_5_, CallbackInfoReturnable<BiomeGenBase[]> cir, @Local(name = "aint") int[] ints,
            @Share("cache") LocalRef<int[]> cache) {
        cache.set(ints);
    }

    @Inject(method = "getBiomesForGeneration", at = @At(value = "RETURN"))
    private void hodgepodge$collectInts(BiomeGenBase[] p_76937_1_, int p_76937_2_, int p_76937_3_, int p_76937_4_,
            int p_76937_5_, CallbackInfoReturnable<BiomeGenBase[]> cir, @Share("cache") LocalRef<int[]> cache) {
        releaseCache(cache.get());
    }

    @Inject(
            method = "getBiomeGenAt([Lnet/minecraft/world/biome/BiomeGenBase;IIIIZ)[Lnet/minecraft/world/biome/BiomeGenBase;",
            at = @At(
                    value = "INVOKE_ASSIGN",
                    target = "Lnet/minecraft/world/gen/layer/GenLayer;getInts(IIII)[I",
                    shift = At.Shift.AFTER))
    private void hodgepodge$shareInts(BiomeGenBase[] p_76931_1_, int p_76931_2_, int p_76931_3_, int p_76931_4_,
            int p_76931_5_, boolean p_76931_6_, CallbackInfoReturnable<BiomeGenBase[]> cir,
            @Local(name = "aint") int[] ints, @Share("cache") LocalRef<int[]> cache) {
        cache.set(ints);
    }

    @Inject(
            method = "getBiomeGenAt([Lnet/minecraft/world/biome/BiomeGenBase;IIIIZ)[Lnet/minecraft/world/biome/BiomeGenBase;",
            at = @At(value = "RETURN", ordinal = 1))
    private void hodgepodge$collectInts(BiomeGenBase[] p_76931_1_, int p_76931_2_, int p_76931_3_, int p_76931_4_,
            int p_76931_5_, boolean p_76931_6_, CallbackInfoReturnable<BiomeGenBase[]> cir,
            @Share("cache") LocalRef<int[]> cache) {
        releaseCache(cache.get());
    }

    @Inject(
            method = "areBiomesViable",
            at = @At(
                    value = "INVOKE_ASSIGN",
                    target = "Lnet/minecraft/world/gen/layer/GenLayer;getInts(IIII)[I",
                    shift = At.Shift.AFTER))
    private void hodgepodge$shareInts(int p_76940_1_, int p_76940_2_, int p_76940_3_, List<BiomeGenBase> p_76940_4_,
            CallbackInfoReturnable<Boolean> cir, @Local(name = "aint") int[] ints,
            @Share("cache") LocalRef<int[]> cache) {
        cache.set(ints);
    }

    @Inject(method = "areBiomesViable", at = @At(value = "RETURN"))
    private void hodgepodge$collectInts(int p_76940_1_, int p_76940_2_, int p_76940_3_, List<BiomeGenBase> p_76940_4_,
            CallbackInfoReturnable<Boolean> cir, @Share("cache") LocalRef<int[]> cache) {
        releaseCache(cache.get());
    }

    @Inject(
            method = "findBiomePosition",
            at = @At(
                    value = "INVOKE_ASSIGN",
                    target = "Lnet/minecraft/world/gen/layer/GenLayer;getInts(IIII)[I",
                    shift = At.Shift.AFTER))
    private void hodgepodge$shareInts(int p_150795_1_, int p_150795_2_, int p_150795_3_, List<BiomeGenBase> p_150795_4_,
            Random p_150795_5_, CallbackInfoReturnable<ChunkPosition> cir, @Local(name = "aint") int[] ints,
            @Share("cache") LocalRef<int[]> cache) {
        cache.set(ints);
    }

    @Inject(method = "findBiomePosition", at = @At(value = "RETURN"))
    private void hodgepodge$collectInts(int p_150795_1_, int p_150795_2_, int p_150795_3_,
            List<BiomeGenBase> p_150795_4_, Random p_150795_5_, CallbackInfoReturnable<ChunkPosition> cir,
            @Share("cache") LocalRef<int[]> cache) {
        releaseCache(cache.get());
    }
}