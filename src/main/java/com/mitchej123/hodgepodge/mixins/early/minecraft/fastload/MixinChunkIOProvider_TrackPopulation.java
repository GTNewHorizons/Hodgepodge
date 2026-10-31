package com.mitchej123.hodgepodge.mixins.early.minecraft.fastload;

import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.mitchej123.hodgepodge.mixins.hooks.ChunkGenScheduler;

/**
 * Tracks unpopulated chunks loaded off-thread, like MixinChunkProviderServer_DeferPopulation does for chunks loaded on
 * the server thread.
 */
@Mixin(targets = "net.minecraftforge.common.chunkio.ChunkIOProvider")
public class MixinChunkIOProvider_TrackPopulation {

    @Redirect(
            method = "callStage2",
            remap = false,
            at = @At(
                    value = "INVOKE",
                    remap = true,
                    target = "Lnet/minecraft/world/chunk/Chunk;populateChunk(Lnet/minecraft/world/chunk/IChunkProvider;Lnet/minecraft/world/chunk/IChunkProvider;II)V"))
    private void hodgepodge$trackPopulation(Chunk chunk, IChunkProvider p1, IChunkProvider p2, int x, int z) {
        final ChunkGenScheduler scheduler = ChunkGenScheduler.forDimension(chunk.worldObj.provider.dimensionId);
        if (scheduler.getPopulationDepth() > 0 && ChunkGenScheduler.hasTickingStarted()) {
            scheduler.deferChunkPopulation(chunk, x, z);
            return;
        }
        scheduler.incrementPopulationDepth();
        try {
            chunk.populateChunk(p1, p2, x, z);
        } finally {
            scheduler.decrementPopulationDepth();
        }
        scheduler.trackIfUnpopulated(chunk, x, z);
    }
}
