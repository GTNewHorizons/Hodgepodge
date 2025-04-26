package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.world.ChunkCoordIntPair;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import com.gtnewhorizon.gtnhlib.hash.Fnv1a32;

@Mixin(ChunkCoordIntPair.class)
public class MixinChunkCoordIntPair_BetterHash {

    @Final
    @Shadow
    public int chunkXPos;
    /** The Z position of this Chunk Coordinate Pair */
    @Final
    @Shadow
    public int chunkZPos;

    /**
     * @author mitchej123
     * @reason Swap out the default hashCode function with a better one
     */
    @Overwrite
    public int hashCode() {
        int hash = Fnv1a32.initialState();
        hash = Fnv1a32.hashStep(hash, chunkXPos);
        hash = Fnv1a32.hashStep(hash, chunkZPos);
        return hash;
    }

}
