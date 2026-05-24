package com.mitchej123.hodgepodge.mixins.late.ic2;

import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import com.falsepattern.endlessids.mixin.helpers.ChunkBiomeHook;

import ic2.core.block.machine.tileentity.TileEntityTerra;

@Mixin(TileEntityTerra.class)
public class MixinIC2TileEntityTerra_EIDCompat {

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
