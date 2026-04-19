package com.mitchej123.hodgepodge.mixins.early.minecraft.fastload;

import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderServer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.mitchej123.hodgepodge.mixins.hooks.ChunkGenScheduler;

/**
 * Prevents cascading chunk population during gameplay. When a populateChunk call triggers a neighbor chunk load that
 * itself wants to populate, this redirect defers the nested population to the scheduler queue instead of executing it
 * synchronously. Only applies after ticking has started; during initial world load, population runs normally.
 */
@Mixin(ChunkProviderServer.class)
public class MixinChunkProviderServer_DeferPopulation {

    @Shadow
    public WorldServer worldObj;

    @Redirect(
            method = "originalLoadChunk",
            remap = false,
            at = @At(
                    value = "INVOKE",
                    remap = true,
                    target = "Lnet/minecraft/world/chunk/Chunk;populateChunk(Lnet/minecraft/world/chunk/IChunkProvider;Lnet/minecraft/world/chunk/IChunkProvider;II)V"))
    private void hodgepodge$deferPopulation(Chunk chunk, IChunkProvider p1, IChunkProvider p2, int x, int z) {
        final var scheduler = ChunkGenScheduler.forDimension(worldObj.provider.dimensionId);

        // Excluded dimensions (e.g. Thaumcraft outer lands) must never defer population, even when called from
        // within another population pass (populationDepth > 0). Deferring neighbour chunks in these dimensions
        // causes incomplete structure generation (e.g. gaps in the eldritch maze where corridor walls that span
        // chunk borders are missing because the neighbour chunk had only terrain when the current chunk was
        // populated). Allow cascading synchronous population for excluded dimensions.
        if (!ChunkGenScheduler.isDimExcludedFromChunkThrottle(worldObj.provider.dimensionId)
                && scheduler.getPopulationDepth() > 0
                && ChunkGenScheduler.hasTickingStarted()) {
            scheduler.deferChunkPopulation(chunk, x, z);
        } else {
            scheduler.incrementPopulationDepth();
            // World gen code (e.g. Thaumcraft maze generation) calls world.getTileEntity() on blocks it just placed,
            // which goes through provideChunk(). If the scheduler is blocked, EntityGuard returns EmptyChunk for
            // unloaded/ungenerated neighbors, causing getTileEntity() to return null and crash. Temporarily unblock
            // so that world gen can access adjacent chunks. Cascading population is still deferred by the depth check.
            final boolean wasBlocked = ChunkGenScheduler.isBlocked();
            if (wasBlocked) {
                ChunkGenScheduler.enableChunkLoads();
            }
            try {
                chunk.populateChunk(p1, p2, x, z);
            } finally {
                if (wasBlocked) {
                    ChunkGenScheduler.disableChunkLoads();
                }
                scheduler.decrementPopulationDepth();
            }
            scheduler.trackIfUnpopulated(chunk, x, z);
        }
    }
}
