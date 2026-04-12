package com.mitchej123.hodgepodge.mixins.early.minecraft.fastload;

import net.minecraft.util.LongHashMap;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.AnvilChunkLoader;
import net.minecraft.world.chunk.storage.IChunkLoader;
import net.minecraft.world.gen.ChunkProviderServer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import com.mitchej123.hodgepodge.mixins.hooks.ChunkGenScheduler;
import com.mitchej123.hodgepodge.util.ChunkPosUtil;

/**
 * Returns EmptyChunk for missing chunks while the scheduler has chunk loads blocked (most of the tick — everything
 * outside the scheduler's own generation window). Already-loaded chunks pass through normally.
 */
@Mixin(value = ChunkProviderServer.class, priority = 500)
public class MixinChunkProviderServer_EntityGuard {

    @Shadow
    public LongHashMap loadedChunkHashMap;

    @Shadow
    private Chunk defaultEmptyChunk;

    @Shadow
    public WorldServer worldObj;

    @Shadow
    public IChunkLoader currentChunkLoader;

    @Shadow
    public boolean loadChunkOnProvideRequest;

    @Shadow
    public Chunk loadChunk(int x, int z) {
        throw new AssertionError();
    }

    /**
     * @author Hodgepodge
     * @reason Return EmptyChunk for missing chunks while scheduler has loads blocked
     */
    @Overwrite
    public Chunk provideChunk(int x, int z) {
        final Chunk chunk = (Chunk) this.loadedChunkHashMap.getValueByKey(ChunkPosUtil.toLong(x, z));
        if (chunk != null) {
            return chunk;
        }

        // Excluded dimensions bypass all throttle guards and always load chunks normally.
        if (ChunkGenScheduler.isDimExcludedFromChunkThrottle(this.worldObj.provider.dimensionId)) {
            return this.loadChunk(x, z);
        }

        // Entity guard: when blocked, don't trigger chunk generation for missing chunks, but allow loading from disk
        if (ChunkGenScheduler.isBlocked() && !this.worldObj.findingSpawnPoint) {
            if (this.currentChunkLoader instanceof AnvilChunkLoader acl && acl.chunkExists(this.worldObj, x, z)) {
                return this.loadChunk(x, z);
            }
            ChunkGenScheduler.incrementBlockedLoadCount();
            return this.defaultEmptyChunk;
        }

        if (this.worldObj.findingSpawnPoint || this.loadChunkOnProvideRequest) {
            return this.loadChunk(x, z);
        }

        return this.defaultEmptyChunk;
    }
}
