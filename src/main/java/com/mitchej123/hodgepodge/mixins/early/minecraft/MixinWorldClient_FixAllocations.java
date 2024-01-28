package com.mitchej123.hodgepodge.mixins.early.minecraft;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.profiler.Profiler;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.storage.ISaveHandler;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.mitchej123.hodgepodge.mixins.interfaces.MutableChunkCoordIntPair;
import com.mitchej123.hodgepodge.util.ChunkPosUtil;

import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;

@Mixin(WorldClient.class)
public abstract class MixinWorldClient_FixAllocations extends World {

    MixinWorldClient_FixAllocations(ISaveHandler saveHandler, String str, WorldProvider provider, WorldSettings settings,
            Profiler profiler) {
        // Dummy cunstructor
        super(saveHandler, str, provider, settings, profiler);
    }

    private final LongSet previousActiveChunkLongSet = new LongOpenHashSet();

    @Redirect(
            method = "func_147456_g",
            at = @At(value = "INVOKE", target = "Ljava/util/Set;retainAll(Ljava/util/Collection;)Z"),
            remap = false)
    private boolean onGetPersistentChunksRetainAll(Set instance, Collection<?> objects) {
        // Redirect retainAll to our set
        final LongSet activeChunkLongSet = ((MixinWorld_FixAllocations) (Object) this).activeChunkLongSet;
        return previousActiveChunkLongSet.retainAll(activeChunkLongSet);
    }

    @Redirect(
            method = "func_147456_g",
            at = @At(value = "INVOKE", target = "Ljava/util/Set;size()I", ordinal = 0),
            remap = false)
    private int getpreviousActiveChunkSetSize(Set instance) {
        // Redirect size to our set
        return previousActiveChunkLongSet.size();
    }

    @Redirect(
            method = "func_147456_g",
            at = @At(value = "INVOKE", target = "Ljava/util/Set;size()I", ordinal = 1),
            remap = false)
    private int getactiveChunkSetSize(Set instance) {
        // Redirect size to our set
        return ((MixinWorld_FixAllocations) (Object) this).activeChunkLongSet.size();
    }

    @Redirect(method = "func_147456_g", at = @At(value = "INVOKE", target = "Ljava/util/Set;clear()V"), remap = false)
    private void onGetPersistentChunksClear(Set instance) {
        // Clear our set instead
        previousActiveChunkLongSet.clear();
    }

    @Redirect(
            method = "func_147456_g",
            at = @At(value = "INVOKE", target = "Ljava/util/Set;contains(Ljava/lang/Object;)Z"),
            remap = false)
    private boolean onGetPersistentChunksContains(Set instance, Object chunkCoordIntPair) {
        // Convert the ChunkCoordIntPair to a long and check if it's in our set
        final ChunkCoordIntPair coordIntPair = (ChunkCoordIntPair) chunkCoordIntPair;
        final long chunkPos = ChunkPosUtil.toLong(coordIntPair.chunkXPos, coordIntPair.chunkZPos);

        return previousActiveChunkLongSet.contains(chunkPos);
    }

    @Redirect(
            method = "func_147456_g",
            at = @At(value = "INVOKE", target = "Ljava/util/Set;iterator()Ljava/util/Iterator;"),
            remap = false)
    private Iterator onGetPersistentChunksIterator(Set instance) {
        // Return the activeChunkLongSet iterator
        return ((MixinWorld_FixAllocations) (Object) this).activeChunkLongSet.iterator();
    }

    // Mutable ChunkCoordIntPair so we can reuse it
    private static MutableChunkCoordIntPair reusableChunkCoordIntPair = (MutableChunkCoordIntPair) new ChunkCoordIntPair(
            0,
            0);

    @Redirect(
            method = "func_147456_g",
            at = @At(value = "INVOKE", target = "Ljava/util/Iterator;next()Ljava/lang/Object;"),
            remap = false)
    private Object onGetPersistentChunksNext(Iterator instance) {
        // Convert the long to a ChunkCoordIntPair, using our reusable object, and return it
        final long chunkPos = ((LongIterator) instance).nextLong();
        reusableChunkCoordIntPair.setChunkXPos(ChunkPosUtil.getPackedX(chunkPos));
        reusableChunkCoordIntPair.setChunkZPos(ChunkPosUtil.getPackedZ(chunkPos));
        return reusableChunkCoordIntPair;
    }

    @Redirect(
            method = "func_147456_g",
            at = @At(value = "INVOKE", target = "Ljava/util/Set;add(Ljava/lang/Object;)Z"),
            remap = false)
    private boolean onGetPersistentChunksAdd(Set instance, Object chunkCoordIntPair) {
        // Convert the ChunkCoordIntPair to a long and add it to our set
        final ChunkCoordIntPair coordIntPair = (ChunkCoordIntPair) chunkCoordIntPair;
        final long chunkPos = ChunkPosUtil.toLong(coordIntPair.chunkXPos, coordIntPair.chunkZPos);

        previousActiveChunkLongSet.add(chunkPos);
        return true;
    }

}
