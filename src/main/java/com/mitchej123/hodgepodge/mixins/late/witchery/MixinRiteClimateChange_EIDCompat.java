package com.mitchej123.hodgepodge.mixins.late.witchery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.emoniph.witchery.blocks.BlockCircle.TileEntityCircle.ActivatedRitual;
import com.emoniph.witchery.ritual.RitualStep.Result;
import com.emoniph.witchery.ritual.rites.RiteClimateChange;
import com.emoniph.witchery.ritual.rites.RiteClimateChange.WeatherChange;
import com.falsepattern.endlessids.mixin.helpers.ChunkBiomeHook;
import com.llamalad7.mixinextras.sugar.Local;

import it.unimi.dsi.fastutil.ints.IntIntImmutablePair;
import it.unimi.dsi.fastutil.ints.IntIntPair;

@Mixin(targets = { "com.emoniph.witchery.ritual.rites.RiteClimateChange$StepClimateChange" })
public class MixinRiteClimateChange_EIDCompat {

    @Unique
    private Map<IntIntPair, short[]> hodgepodge$biomeOverrides;

    @Inject(method = "process", at = @At(value = "HEAD"), remap = false)
    private void hodgepodge$setBiomeOverrides(World world, int posX, int posY, int posZ, long ticks,
            ActivatedRitual ritual, CallbackInfoReturnable<Result> cir) {
        hodgepodge$biomeOverrides = new HashMap<>();
    }

    @Inject(
            method = "process",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/network/play/server/S26PacketMapChunkBulk;<init>(Ljava/util/List;)V"))
    private void hodgepodge$setBiomeArrayProperly(World world, int posX, int posY, int posZ, long ticks,
            ActivatedRitual ritual, CallbackInfoReturnable<Result> cir,
            @Local(name = "chunks") ArrayList<Chunk> chunks) {
        for (Map.Entry<IntIntPair, short[]> entry : hodgepodge$biomeOverrides.entrySet()) {
            IntIntPair chunkCoords = entry.getKey();
            Chunk chunk = world.getChunkFromChunkCoords(chunkCoords.leftInt(), chunkCoords.rightInt());
            ((ChunkBiomeHook) chunk).setBiomeShortArray(entry.getValue());
            chunks.add(chunk);
        }
        hodgepodge$biomeOverrides = null;
    }

    /**
     * @author tiffit
     * @reason use EID biome arrays
     *
     *         The chunkMap is ignored in favor of hodgepodge$biomeOverrides
     */
    @Overwrite(remap = false)
    protected void drawLine(World world, int x1, int x2, int z, HashMap<?, byte[]> chunkMap, WeatherChange weather,
            int biomeID) {
        for (int x = x1; x <= x2; ++x) {
            IntIntPair coord = IntIntImmutablePair.of(x >> 4, z >> 4);
            short[] map = hodgepodge$biomeOverrides.get(coord);
            if (map == null) {
                Chunk chunk = world.getChunkFromBlockCoords(x, z);
                map = ((ChunkBiomeHook) chunk).getBiomeShortArray().clone();
                hodgepodge$biomeOverrides.put(coord, map);
            }

            map[(z & 15) << 4 | x & 15] = (short) biomeID;
            if (weather == RiteClimateChange.WeatherChange.SUN) {
                int y = world.getTopSolidOrLiquidBlock(x, z);
                if (world.getBlock(x, y, z) == Blocks.snow) {
                    world.setBlockToAir(x, y, z);
                }
            }
        }

    }

}
