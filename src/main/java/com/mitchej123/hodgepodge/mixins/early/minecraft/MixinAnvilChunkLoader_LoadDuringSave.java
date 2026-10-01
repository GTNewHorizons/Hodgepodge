package com.mitchej123.hodgepodge.mixins.early.minecraft;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.world.chunk.storage.AnvilChunkLoader;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Vanilla removes a chunk from the pending save list before writing it, so a load in that window reads the previous
 * version from disk (or regenerates the chunk if it was never written). Keep the chunk visible until it is written.
 */
@Mixin(AnvilChunkLoader.class)
public abstract class MixinAnvilChunkLoader_LoadDuringSave {

    @Shadow
    private List<?> chunksToRemove;

    @Shadow
    private Set<ChunkCoordIntPair> pendingAnvilChunksCoordinates;

    @Shadow
    private Object syncLockObject;

    @Shadow(remap = false)
    protected abstract Object[] checkedReadChunkFromNBT__Async(World world, int x, int z, NBTTagCompound nbt);

    /** Chunks taken off the pending list whose write has not finished yet. Guarded by syncLockObject. */
    @Unique
    private final Map<ChunkCoordIntPair, NBTTagCompound> hodgepodge$inFlight = new HashMap<>();

    /** The pending chunk the current thread is writing (the IO thread and a save-all flush can both write). */
    @Unique
    private static final ThreadLocal<AccessorAnvilChunkLoaderPendingChunk> hodgepodge$writing = new ThreadLocal<>();

    @Redirect(
            method = "writeNextIO",
            at = @At(value = "INVOKE", target = "Ljava/util/List;remove(I)Ljava/lang/Object;"))
    private Object hodgepodge$markInFlight(List<?> list, int index) {
        // Called inside synchronized (syncLockObject)
        Object removed = list.remove(index);
        AccessorAnvilChunkLoaderPendingChunk pending = (AccessorAnvilChunkLoaderPendingChunk) removed;
        hodgepodge$inFlight.put(pending.hodgepodge$getChunkCoordinate(), pending.hodgepodge$getNbtTags());
        hodgepodge$writing.set(pending);
        return removed;
    }

    @Inject(method = "writeNextIO", at = @At("RETURN"))
    private void hodgepodge$clearInFlight(CallbackInfoReturnable<Boolean> cir) {
        AccessorAnvilChunkLoaderPendingChunk pending = hodgepodge$writing.get();
        if (pending == null) return;
        hodgepodge$writing.remove();
        ChunkCoordIntPair coords = pending.hodgepodge$getChunkCoordinate();
        synchronized (syncLockObject) {
            // A newer save of the same chunk may have been taken meanwhile; only remove our own entry.
            if (hodgepodge$inFlight.get(coords) == pending.hodgepodge$getNbtTags()) {
                hodgepodge$inFlight.remove(coords);
            }
        }
    }

    @Inject(method = "loadChunk__Async", at = @At("HEAD"), cancellable = true, remap = false)
    private void hodgepodge$loadInFlight(World world, int x, int z, CallbackInfoReturnable<Object[]> cir) {
        ChunkCoordIntPair coords = new ChunkCoordIntPair(x, z);
        NBTTagCompound nbt = null;
        synchronized (syncLockObject) {
            if (hodgepodge$inFlight.isEmpty()) return;
            // Look at the pending list and the in-flight map under one lock, so the chunk can't move between them.
            if (pendingAnvilChunksCoordinates.contains(coords)) {
                for (Object o : chunksToRemove) {
                    AccessorAnvilChunkLoaderPendingChunk pending = (AccessorAnvilChunkLoaderPendingChunk) o;
                    if (coords.equals(pending.hodgepodge$getChunkCoordinate())) {
                        nbt = pending.hodgepodge$getNbtTags();
                        break;
                    }
                }
            }
            if (nbt == null) {
                nbt = hodgepodge$inFlight.get(coords);
                if (nbt == null) return;
            }
        }
        cir.setReturnValue(checkedReadChunkFromNBT__Async(world, x, z, nbt));
    }

    @Inject(method = "chunkExists", at = @At("HEAD"), cancellable = true, remap = false)
    private void hodgepodge$existsInFlight(World world, int x, int z, CallbackInfoReturnable<Boolean> cir) {
        synchronized (syncLockObject) {
            if (!hodgepodge$inFlight.isEmpty() && hodgepodge$inFlight.containsKey(new ChunkCoordIntPair(x, z))) {
                cir.setReturnValue(true);
            }
        }
    }
}
