package com.mitchej123.hodgepodge.mixins.early.minecraft;

import java.util.Iterator;
import java.util.List;

import net.minecraft.client.multiplayer.ChunkProviderClient;
import net.minecraft.util.LongHashMap;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.mitchej123.hodgepodge.util.FastUtilLongHashMap;

/*
 * Speed up ChunkProviderClient by removing usage of the chunkListing list (slow removals) which is identical to the
 * chunkMapping values, and instead use an iterator from the chunkMapping values directly. NOTE: Depends on the ASM
 * Transformer `SpeedupLongIntHashMapTransformer` to replace the LongHashMap with FastUtilLongHashMap.
 */
@SuppressWarnings("rawtypes")
@Mixin(ChunkProviderClient.class)
public class MixinChunkProviderClient_RemoveChunkListing {

    @Shadow
    private LongHashMap chunkMapping;

    @Shadow
    private List chunkListing = null;

    @Redirect(
            method = "unloadChunk",
            at = @At(value = "INVOKE", target = "Ljava/util/List;remove(Ljava/lang/Object;)Z", remap = false))
    private boolean hodgepodge$unloadChunkRemoveChunkListing(List instance, Object o) {
        // NOOP
        return true;
    }

    @Redirect(
            method = "loadChunk",
            at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z", remap = false))
    private boolean hodgepodge$loadChunkRemoveChunkListing(List instance, Object o) {
        // NOOP
        return true;
    }

    @Redirect(
            method = "unloadQueuedChunks",
            at = @At(value = "INVOKE", target = "Ljava/util/List;iterator()Ljava/util/Iterator;", remap = false))
    private Iterator hodgepodge$getValuesIterator(List instance) {
        // Get the iterator from the FastUtilLongHashMap instead of the chunkListing list
        return ((FastUtilLongHashMap) chunkMapping).valuesIterator();
    }

    @Redirect(method = "makeString", at = @At(value = "INVOKE", target = "Ljava/util/List;size()I", remap = false))
    private int hodgepodge$makeStringGetLoadedChunkCount(List instance) {
        // Return the size of the FastUtilLongHashMap instead of the chunkListing list
        return chunkMapping.getNumHashElements();
    }

    @Redirect(
            method = "getLoadedChunkCount",
            at = @At(value = "INVOKE", target = "Ljava/util/List;size()I", remap = false))
    private int hodgepodge$getLoadedChunkCount(List instance) {
        // Return the size of the FastUtilLongHashMap instead of the chunkListing list
        return chunkMapping.getNumHashElements();
    }

}
