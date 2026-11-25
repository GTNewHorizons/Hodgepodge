package com.mitchej123.hodgepodge.mixins.late.biomesoplenty;

import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import com.falsepattern.endlessids.mixin.helpers.ChunkBiomeHook;

import biomesoplenty.common.utils.BiomeUtils;

@Mixin(BiomeUtils.class)
public class MixinBOPBiomeUtils_EIDCompat {

    /**
     * @author tiffit
     * @reason EID compat
     */
    @Overwrite(remap = false)
    public static void setBiomeAt(World world, int x, int z, BiomeGenBase biome) {
        Chunk chunk = world.getChunkFromBlockCoords(x, z);
        short[] array = ((ChunkBiomeHook) chunk).getBiomeShortArray();
        array[(z & 15) << 4 | x & 15] = (short) (biome.biomeID & 0xff_ff);
        ((ChunkBiomeHook) chunk).setBiomeShortArray(array);
    }
}
