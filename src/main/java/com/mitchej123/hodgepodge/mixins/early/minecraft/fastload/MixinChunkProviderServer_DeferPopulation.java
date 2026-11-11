package com.mitchej123.hodgepodge.mixins.early.minecraft.fastload;

import net.minecraft.util.LongHashMap;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderServer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mitchej123.hodgepodge.mixins.hooks.ChunkGenScheduler;
import com.mitchej123.hodgepodge.util.ChunkPosUtil;

/**
 * Counts population depth at the populate choke point: chunk generation is admitted while a population runs, and once
 * ticking has started a population nested inside another is deferred to the scheduler queue.
 */
@Mixin(ChunkProviderServer.class)
public class MixinChunkProviderServer_DeferPopulation {

    @Shadow
    public WorldServer worldObj;

    @Shadow
    public LongHashMap loadedChunkHashMap;

    @WrapMethod(method = "populate")
    private void hodgepodge$countPopulationDepth(IChunkProvider provider, int x, int z, Operation<Void> original) {
        final var scheduler = ChunkGenScheduler.forDimension(worldObj.provider.dimensionId);
        if (scheduler.getPopulationDepth() > 0 && ChunkGenScheduler.hasTickingStarted()) {
            final Chunk chunk = (Chunk) loadedChunkHashMap.getValueByKey(ChunkPosUtil.toLong(x, z));
            if (chunk != null) scheduler.trackIfUnpopulated(chunk, x, z);
            return;
        }
        scheduler.incrementPopulationDepth();
        try {
            original.call(provider, x, z);
        } finally {
            scheduler.decrementPopulationDepth();
        }
    }

    @WrapOperation(
            method = "originalLoadChunk",
            remap = false,
            at = @At(
                    value = "INVOKE",
                    remap = true,
                    target = "Lnet/minecraft/world/chunk/Chunk;populateChunk(Lnet/minecraft/world/chunk/IChunkProvider;Lnet/minecraft/world/chunk/IChunkProvider;II)V"))
    private void hodgepodge$trackUnpopulated(Chunk chunk, IChunkProvider p1, IChunkProvider p2, int x, int z,
            Operation<Void> original) {
        original.call(chunk, p1, p2, x, z);
        ChunkGenScheduler.forDimension(worldObj.provider.dimensionId).trackIfUnpopulated(chunk, x, z);
    }
}
