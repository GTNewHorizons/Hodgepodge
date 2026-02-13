package com.mitchej123.hodgepodge.mixins.early.minecraft.fastload;

import java.util.List;
import java.util.Set;

import net.minecraft.util.LongHashMap;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.chunk.storage.IChunkLoader;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.ForgeChunkManager;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mitchej123.hodgepodge.config.SpeedupsConfig;

import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;

@Mixin(ChunkProviderServer.class)
public class MixinChunkProviderServer_FastUnload {

    @Shadow
    private Set chunksToUnload;

    @Shadow
    public LongHashMap loadedChunkHashMap;

    @Shadow
    public List<Chunk> loadedChunks;

    @Shadow
    public WorldServer worldObj;

    @Shadow
    public IChunkLoader currentChunkLoader;

    @Shadow
    public IChunkProvider currentChunkProvider;

    @Shadow
    private void safeSaveChunk(Chunk chunk) {}

    @Shadow
    private void safeSaveExtraChunkData(Chunk chunk) {}

    @Inject(method = "<init>", at = @At("TAIL"))
    private void hodgepodge$replaceChunksToUnload(WorldServer world, IChunkLoader loader, IChunkProvider provider,
            CallbackInfo ci) {
        this.chunksToUnload = new LongOpenHashSet();
    }

    /**
     * @author mitchej123
     * @reason Less Slow
     */
    @Overwrite
    public boolean unloadQueuedChunks() {
        if (!this.worldObj.levelSaving) {
            final var persistentChunks = this.worldObj.getPersistentChunks();
            final LongOpenHashSet unloadSet = (LongOpenHashSet) this.chunksToUnload;
            if (!unloadSet.isEmpty() && !persistentChunks.isEmpty()) {
                for (ChunkCoordIntPair forced : persistentChunks.keySet()) {
                    unloadSet.remove(ChunkCoordIntPair.chunkXZ2Int(forced.chunkXPos, forced.chunkZPos));
                }
            }

            final int limit = SpeedupsConfig.maxUnloadSpeed;

            if (!unloadSet.isEmpty()) {
                final LongIterator iter = unloadSet.iterator();
                ObjectOpenHashSet<Chunk> chunksToRemove = null;
                boolean dimensionUnloaded = false;
                int count = 0;

                while (iter.hasNext() && count < limit) {
                    long key = iter.nextLong();
                    iter.remove();
                    count++;

                    final Chunk chunk = (Chunk) this.loadedChunkHashMap.getValueByKey(key);
                    if (chunk != null) {
                        chunk.onChunkUnload();
                        this.safeSaveChunk(chunk);
                        this.safeSaveExtraChunkData(chunk);
                        ForgeChunkManager.putDormantChunk(key, chunk);
                        this.loadedChunkHashMap.remove(key);
                        if (chunksToRemove == null) chunksToRemove = new ObjectOpenHashSet<>();
                        chunksToRemove.add(chunk);

                        if (loadedChunks.size() == chunksToRemove.size() && persistentChunks.isEmpty()
                                && !DimensionManager.shouldLoadSpawn(this.worldObj.provider.dimensionId)) {
                            DimensionManager.unloadWorld(this.worldObj.provider.dimensionId);
                            dimensionUnloaded = true;
                            break;
                        }
                    }
                }

                if (chunksToRemove != null) {
                    this.loadedChunks.removeAll(chunksToRemove);
                }

                if (dimensionUnloaded) {
                    return this.currentChunkProvider.unloadQueuedChunks();
                }
            }

            if (this.currentChunkLoader != null) {
                this.currentChunkLoader.chunkTick();
            }
        }

        return this.currentChunkProvider.unloadQueuedChunks();
    }
}
