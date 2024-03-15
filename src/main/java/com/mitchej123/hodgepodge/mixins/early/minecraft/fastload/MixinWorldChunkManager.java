package com.mitchej123.hodgepodge.mixins.early.minecraft.fastload;

import java.util.List;
import java.util.Random;

import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.util.ReportedException;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManager;
import net.minecraft.world.gen.layer.GenLayer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.sugar.Local;
import com.mitchej123.hodgepodge.server.NewIntCache;

@Mixin(WorldChunkManager.class)
public class MixinWorldChunkManager {

    @Shadow
    private GenLayer genBiomes;

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
                    shift = At.Shift.AFTER),
            cancellable = true)
    private void hodgepodge$recycleCacheRain(float[] downfalls, int x, int z, int width, int height,
            CallbackInfoReturnable<float[]> cir, @Local(name = "aint") int[] ints) {

        for (int i = 0; i < width * height; ++i) {
            try {
                float f = (float) BiomeGenBase.getBiome(ints[i]).getIntRainfall() / 65536.0F;

                if (f > 1.0F) {
                    f = 1.0F;
                }

                downfalls[i] = f;
            } catch (Throwable throwable) {
                CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Invalid Biome id");
                CrashReportCategory crashreportcategory = crashreport.makeCategory("DownfallBlock");
                crashreportcategory.addCrashSection("biome id", i);
                crashreportcategory.addCrashSection("downfalls[] size", downfalls.length);
                crashreportcategory.addCrashSection("x", x);
                crashreportcategory.addCrashSection("z", z);
                crashreportcategory.addCrashSection("w", width);
                crashreportcategory.addCrashSection("h", height);
                throw new ReportedException(crashreport);
            }
        }

        NewIntCache.releaseCache(ints);
        cir.setReturnValue(downfalls);
    }

    @Inject(
            method = "getBiomesForGeneration",
            at = @At(
                    value = "INVOKE_ASSIGN",
                    target = "Lnet/minecraft/world/gen/layer/GenLayer;getInts(IIII)[I",
                    shift = At.Shift.AFTER),
            cancellable = true)
    private void hodgepodge$recycleCacheBiomes(BiomeGenBase[] biomes, int x, int z, int width, int height,
            CallbackInfoReturnable<BiomeGenBase[]> cir, @Local(name = "aint") int[] ints) {

        try {
            for (int i = 0; i < width * height; ++i) {
                biomes[i] = BiomeGenBase.getBiome(ints[i]);
            }

            NewIntCache.releaseCache(ints);
            cir.setReturnValue(biomes);
        } catch (Throwable throwable) {
            CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Invalid Biome id");
            CrashReportCategory crashreportcategory = crashreport.makeCategory("RawBiomeBlock");
            crashreportcategory.addCrashSection("biomes[] size", biomes.length);
            crashreportcategory.addCrashSection("x", x);
            crashreportcategory.addCrashSection("z", z);
            crashreportcategory.addCrashSection("w", width);
            crashreportcategory.addCrashSection("h", height);
            throw new ReportedException(crashreport);
        }
    }

    @Inject(
            method = "getBiomeGenAt([Lnet/minecraft/world/biome/BiomeGenBase;IIIIZ)[Lnet/minecraft/world/biome/BiomeGenBase;",
            at = @At(
                    value = "INVOKE_ASSIGN",
                    target = "Lnet/minecraft/world/gen/layer/GenLayer;getInts(IIII)[I",
                    shift = At.Shift.AFTER),
            cancellable = true)
    private void hodgepodge$recycleCacheBiomeAt(BiomeGenBase[] biomes, int p_76931_2_, int p_76931_3_, int width,
            int height, boolean p_76931_6_, CallbackInfoReturnable<BiomeGenBase[]> cir,
            @Local(name = "aint") int[] ints) {

        for (int i = 0; i < width * height; ++i) {
            biomes[i] = BiomeGenBase.getBiome(ints[i]);
        }

        NewIntCache.releaseCache(ints);
        cir.setReturnValue(biomes);
    }

    @Inject(
            method = "areBiomesViable",
            at = @At(
                    value = "INVOKE_ASSIGN",
                    target = "Lnet/minecraft/world/gen/layer/GenLayer;getInts(IIII)[I",
                    shift = At.Shift.AFTER),
            cancellable = true)
    private void hodgepodge$recycleCacheViable(int x, int z, int radius, List<BiomeGenBase> allowed,
            CallbackInfoReturnable<Boolean> cir, @Local(name = "l1") int areaWidth, @Local(name = "i2") int areaHeight,
            @Local(ordinal = 0) int[] cache) {

        try {
            for (int i = 0; i < areaWidth * areaHeight; ++i) {
                BiomeGenBase biomegenbase = BiomeGenBase.getBiome(cache[i]);

                if (!allowed.contains(biomegenbase)) {

                    NewIntCache.releaseCache(cache);
                    cir.setReturnValue(false);
                    return;
                }
            }

            NewIntCache.releaseCache(cache);
            cir.setReturnValue(true);
        } catch (Throwable throwable) {
            CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Invalid Biome id");
            CrashReportCategory crashreportcategory = crashreport.makeCategory("Layer");
            crashreportcategory.addCrashSection("Layer", this.genBiomes.toString());
            crashreportcategory.addCrashSection("x", x);
            crashreportcategory.addCrashSection("z", z);
            crashreportcategory.addCrashSection("radius", radius);
            crashreportcategory.addCrashSection("allowed", allowed);
            throw new ReportedException(crashreport);
        }
    }

    @Inject(
            method = "findBiomePosition",
            at = @At(
                    value = "INVOKE_ASSIGN",
                    target = "Lnet/minecraft/world/gen/layer/GenLayer;getInts(IIII)[I",
                    shift = At.Shift.AFTER),
            cancellable = true)
    private void hodgepodge$recycleCacheFindBiome(int x, int z, int radius, List<BiomeGenBase> p_150795_4_,
            Random p_150795_5_, CallbackInfoReturnable<ChunkPosition> cir, @Local(name = "l1") int l1,
            @Local(name = "i2") int i2, @Local(name = "l") int l, @Local(name = "i1") int i1,
            @Local(ordinal = 0) int[] ints) {

        ChunkPosition chunkposition = null;
        int j2 = 0;

        for (int i = 0; i < l1 * i2; ++i) {
            int l2 = l + i % l1 << 2;
            int i3 = i1 + i / l1 << 2;
            BiomeGenBase biomegenbase = BiomeGenBase.getBiome(ints[i]);

            if (p_150795_4_.contains(biomegenbase) && (chunkposition == null || p_150795_5_.nextInt(j2 + 1) == 0)) {
                chunkposition = new ChunkPosition(l2, 0, i3);
                ++j2;
            }
        }

        NewIntCache.releaseCache(ints);
        cir.setReturnValue(chunkposition);
    }
}
