package com.mitchej123.hodgepodge.mixins.early.minecraft.fastload;

import static com.mitchej123.hodgepodge.Common.log;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.profiler.Profiler;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldSettings;
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

import com.mitchej123.hodgepodge.hax.LongChunkCoordIntPairSet;
import com.mitchej123.hodgepodge.mixins.interfaces.FastWorldServer;
import com.mitchej123.hodgepodge.server.FastCPS;
import com.mitchej123.hodgepodge.util.ChunkPosUtil;

import it.unimi.dsi.fastutil.longs.Long2ObjectMaps;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;

@Mixin(WorldServer.class)
public abstract class MixinWorldServer extends World implements FastWorldServer {

    @Shadow
    public ChunkProviderServer theChunkProviderServer;
    @Unique
    private final Long2ObjectOpenHashMap<CompletableFuture<Chunk>> hodgepodge$chunksToLoad = new Long2ObjectOpenHashMap<>();
    @Unique
    private final Long2ObjectOpenHashMap<Chunk> hodgepodge$chunks = new Long2ObjectOpenHashMap<>();
    @Unique
    private int hodgepodge$numNewGen = 0;
    @Unique
    private final int hodgepodge$maxNewGen = 100;
    @Unique
    private int hodgepodge$overLoad = 0;
    @Unique
    private int hodgepodge$properActive = 0;
    @Unique
    private int hodgepodge$realActive = 0;
    @Unique
    private final Set<EntityPlayerMP> hodgepodge$partialRendering = new ObjectOpenHashSet<>();
    @Unique
    private boolean hodgepodge$flag = false;
    @Unique
    private LongChunkCoordIntPairSet hodgepodge$activeChunks2 = new LongChunkCoordIntPairSet();

    @Override
    public boolean isThrottlingGen() {
        return this.hodgepodge$numNewGen >= this.hodgepodge$maxNewGen;
    }

    @Override
    public int remainingGenBudget() {
        return this.hodgepodge$maxNewGen - this.hodgepodge$numNewGen;
    }

    @Override
    public void spendGenBudget(int amount) {
        this.hodgepodge$numNewGen += amount;
    }

    @Redirect(
            method = "createChunkProvider",
            at = @At(
                    value = "NEW",
                    target = "(Lnet/minecraft/world/WorldServer;Lnet/minecraft/world/chunk/storage/IChunkLoader;Lnet/minecraft/world/chunk/IChunkProvider;)Lnet/minecraft/world/gen/ChunkProviderServer;"))
    private ChunkProviderServer hodgepodge$replaceChunkProvider(WorldServer server, IChunkLoader loader,
            IChunkProvider backingCP) {
        return new FastCPS(server, (AnvilChunkLoader) loader, backingCP);
    }

    @Inject(
            method = "func_147456_g",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;func_147456_g()V", shift = At.Shift.AFTER))
    private void hodgepodge$threadChunkGen(CallbackInfo ci) {

        if (this.hodgepodge$properActive != this.hodgepodge$realActive) {

            log.warn(
                    "{} active chunks last tick, should have been {}",
                    this.hodgepodge$realActive,
                    this.hodgepodge$properActive);
        }
        if (this.hodgepodge$overLoad != 0) {
            log.warn("{} excess chunks loaded last tick", this.hodgepodge$overLoad);
            this.hodgepodge$overLoad = 0;
        }

        this.hodgepodge$flag = false;

        this.hodgepodge$chunks.clear();
        this.hodgepodge$chunksToLoad.clear();
        this.hodgepodge$numNewGen = 0;
        this.hodgepodge$properActive = this.activeChunkSet.size();
        this.hodgepodge$realActive = 0;
        this.hodgepodge$activeChunks2.clear();

        // Queue chunks on worker threads or main
        final ChunkProviderServer cps = this.theChunkProviderServer;
        final AnvilChunkLoader acl = (AnvilChunkLoader) cps.currentChunkLoader;
        for (LongIterator i = ((LongChunkCoordIntPairSet) this.activeChunkSet).longIterator(); i.hasNext();) {
            final long key = i.nextLong();
            final int cx = ChunkPosUtil.getPackedX(key);
            final int cz = ChunkPosUtil.getPackedZ(key);

            // If already loaded, just return it
            if (cps.loadedChunkHashMap.containsItem(key)) {

                final CompletableFuture<Chunk> cf = new CompletableFuture<>();
                cf.complete((Chunk) cps.loadedChunkHashMap.getValueByKey(key));
                this.hodgepodge$activeChunks2.addLong(key);
                this.hodgepodge$chunksToLoad.put(key, cf);
            } else if (acl.chunkExists(this, cx, cz)) {

                // The chunk exists on disk, but needs to be loaded. Trivially threaded, forge already has functions for
                // that.
                final CompletableFuture<Chunk> cf = new CompletableFuture<>();
                ((FastCPS) this.theChunkProviderServer).queueDiskLoad(cx, cz, key, cf);
                this.hodgepodge$activeChunks2.addLong(key);
                this.hodgepodge$chunksToLoad.put(key, cf);
            } else {

                // Throttle new generation
                if (this.hodgepodge$numNewGen < this.hodgepodge$maxNewGen) {
                    // These chunks need to be generated; let's try that
                    final CompletableFuture<Chunk> cf = new CompletableFuture<>();
                    ((FastCPS) this.theChunkProviderServer).queueGenerate(cx, cz, key, cf);
                    this.hodgepodge$activeChunks2.addLong(key);
                    this.hodgepodge$chunksToLoad.put(key, cf);
                    ++this.hodgepodge$numNewGen;
                }
            }
        }

        // This little shuffle is needed to swap two variables
        LongChunkCoordIntPairSet tmp = (LongChunkCoordIntPairSet) this.activeChunkSet;
        this.activeChunkSet = this.hodgepodge$activeChunks2;
        this.hodgepodge$activeChunks2 = tmp;

        // All mChunk updates need to finish before the tick ends, to avoid CMEs... at least for now.
        // Guarantee that no chunks are still processing before moving on
        Long2ObjectMaps.fastForEach(this.hodgepodge$chunksToLoad, e -> {
            try {
                this.hodgepodge$chunks.put(e.getLongKey(), e.getValue().get());
            } catch (InterruptedException | ExecutionException ex) {
                throw new RuntimeException(ex);
            }
        });

        if (this.hodgepodge$numNewGen != 0) {
            log.info("Generated {} new chunks.", this.hodgepodge$numNewGen);
            log.info("Loading {} chunks.", this.activeChunkSet.size());
        }
    }

    @Redirect(
            method = "func_147456_g",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/WorldServer;getChunkFromChunkCoords(II)Lnet/minecraft/world/chunk/Chunk;"))
    private Chunk hodgepodge$threadChunkGen(WorldServer instance, int cx, int cz) {

        if (!this.hodgepodge$flag && this.isThrottlingGen()) {
            this.hodgepodge$flag = true;
            log.warn("{} active chunks", this.activeChunkSet.size());
        }

        ++this.hodgepodge$realActive;
        final Chunk ch = this.hodgepodge$chunks.get(ChunkCoordIntPair.chunkXZ2Int(cx, cz));
        if (ch != null) return ch;

        // log.warn("New chunk at x: {} z: {}", cx, cz);
        ++this.hodgepodge$overLoad;
        return this.chunkProvider.provideChunk(cx, cz);
    }

    public MixinWorldServer(ISaveHandler p_i45368_1_, String p_i45368_2_, WorldProvider p_i45368_3_,
            WorldSettings p_i45368_4_, Profiler p_i45368_5_) {
        super(p_i45368_1_, p_i45368_2_, p_i45368_3_, p_i45368_4_, p_i45368_5_);
    }
}
