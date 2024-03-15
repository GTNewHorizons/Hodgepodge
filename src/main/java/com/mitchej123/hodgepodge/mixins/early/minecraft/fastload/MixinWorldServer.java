package com.mitchej123.hodgepodge.mixins.early.minecraft.fastload;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import net.minecraft.profiler.Profiler;
import net.minecraft.world.*;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.chunk.storage.AnvilChunkLoader;
import net.minecraft.world.chunk.storage.IChunkLoader;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraft.world.storage.ISaveHandler;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mitchej123.hodgepodge.server.FastCPS;

import it.unimi.dsi.fastutil.objects.Object2ObjectMaps;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

@Mixin(WorldServer.class)
public abstract class MixinWorldServer extends World {

    @Shadow
    public ChunkProviderServer theChunkProviderServer;
    @Unique
    private final Object2ObjectOpenHashMap<ChunkCoordIntPair, CompletableFuture<Chunk>> hodgepodge$chunksToLoad = new Object2ObjectOpenHashMap<>();
    @Unique
    private final Object2ObjectOpenHashMap<ChunkCoordIntPair, Chunk> hodgepodge$chunks = new Object2ObjectOpenHashMap<>();

    @WrapOperation(
            method = "createChunkProvider",
            at = @At(
                    value = "NEW",
                    target = "(Lnet/minecraft/world/WorldServer;Lnet/minecraft/world/chunk/storage/IChunkLoader;Lnet/minecraft/world/chunk/IChunkProvider;)Lnet/minecraft/world/gen/ChunkProviderServer;"))
    private ChunkProviderServer hodgepodge$replaceChunkProvider(WorldServer server, IChunkLoader loader,
            IChunkProvider backingCP, Operation<ChunkProviderServer> original) {
        return new FastCPS(server, (AnvilChunkLoader) loader, backingCP);
    }

    @Inject(
            method = "func_147456_g",
            at = @At(value = "INVOKE", target = "Ljava/util/Set;iterator()Ljava/util/Iterator;"))
    private void hodgepodge$threadChunkGen(CallbackInfo ci) {

        this.hodgepodge$chunksToLoad.clear();

        // Queue chunks on worker threads or main
        final ChunkProviderServer cps = this.theChunkProviderServer;
        final AnvilChunkLoader acl = (AnvilChunkLoader) cps.currentChunkLoader;
        for (ChunkCoordIntPair c : this.activeChunkSet) {

            final long key = ChunkCoordIntPair.chunkXZ2Int(c.chunkXPos, c.chunkZPos);
            final CompletableFuture<Chunk> cf = new CompletableFuture<>();

            // If already loaded, just return it
            if (cps.loadedChunkHashMap.containsItem(key)) {

                cf.complete((Chunk) cps.loadedChunkHashMap.getValueByKey(key));
            } else if (acl.chunkExists(this, c.chunkXPos, c.chunkZPos)) {

                // The chunk exists on disk, but needs to be loaded. Trivially threaded, forge already has functions for
                // that.
                ((FastCPS) this.theChunkProviderServer).queueDiskLoad(c.chunkXPos, c.chunkZPos, key, cf);
            } else {

                // These chunks need to be generated; let's try that
                ((FastCPS) this.theChunkProviderServer).queueGenerate(c.chunkXPos, c.chunkZPos, key, cf);
            }

            this.hodgepodge$chunksToLoad.put(c, cf);
        }

        // All mChunk updates need to finish before the tick ends, to avoid CMEs... at least for now.
        // Guarantee that no chunks are still processing before moving on
        Object2ObjectMaps.fastForEach(this.hodgepodge$chunksToLoad, e -> {
            try {
                this.hodgepodge$chunks.put(e.getKey(), e.getValue().get());
            } catch (InterruptedException | ExecutionException ex) {
                throw new RuntimeException(ex);
            }
        });
    }

    @Redirect(
            method = "func_147456_g",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/WorldServer;getChunkFromChunkCoords(II)Lnet/minecraft/world/chunk/Chunk;"))
    private Chunk hodgepodge$threadChunkGen(WorldServer instance, int cx, int cz,
            @Local(ordinal = 0) ChunkCoordIntPair c) {

        final Chunk ch = this.hodgepodge$chunks.get(c);
        return ch != null ? ch : this.chunkProvider.provideChunk(cx, cz);
    }

    public MixinWorldServer(ISaveHandler p_i45368_1_, String p_i45368_2_, WorldProvider p_i45368_3_,
            WorldSettings p_i45368_4_, Profiler p_i45368_5_) {
        super(p_i45368_1_, p_i45368_2_, p_i45368_3_, p_i45368_4_, p_i45368_5_);
    }
}
