package com.mitchej123.hodgepodge.server;

import static com.mitchej123.hodgepodge.Common.log;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import net.minecraft.block.Block;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.util.ReportedException;
import net.minecraft.world.*;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.EmptyChunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.chunk.storage.AnvilChunkLoader;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.ChunkDataEvent;

import cpw.mods.fml.common.registry.GameRegistry;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

public class FastCPS extends ChunkProviderServer {

    /**
     * used by unload100OldestChunks to iterate the loadedChunkHashMap for unload (underlying assumption, first in,
     * first out)
     */
    private final LongOpenHashSet chunksToUnload = new LongOpenHashSet();
    private final LongOpenHashSet loadingChunks = new LongOpenHashSet();
    private final Chunk empty;
    private int maxUnloadsPerTick = 500;

    private final ThreadLocal<Boolean> isMChunk = ThreadLocal.withInitial(() -> false);
    private final ExecutorService mChunk = Executors.newSingleThreadExecutor();
    private final ExecutorService mPregen = Executors.newSingleThreadExecutor();
    private final int maxWorkers = 6;
    private final ExecutorService workers = Executors.newFixedThreadPool(maxWorkers);
    // A thread-local copy of the backing world generator
    private final ThreadLocal<IChunkProvider> localProvider = ThreadLocal
            .withInitial(() -> this.worldObj.provider.createChunkGenerator());

    public FastCPS(WorldServer worldObj, AnvilChunkLoader loader, IChunkProvider backingCP) {
        super(worldObj, loader, backingCP);

        this.loadedChunks = new ObjectArrayList<>();

        this.empty = new EmptyChunk(worldObj, 0, 0);
        this.mChunk.execute(() -> this.isMChunk.set(true));
    }

    public void queueDiskLoad(int cx, int cz, long key, CompletableFuture<Chunk> chunkf) {
        this.workers.execute(() -> {
            final ChunkAndNbt cnbt = this.loadChunkFromDisk(cx, cz);
            this.mChunk.execute(() -> chunkf.complete(this.finishChunkFromDisk(cnbt, cx, cz, key)));
        });
    }

    public void queueGenerate(int cx, int cz, long key, CompletableFuture<Chunk> cf) {

        this.mPregen.execute(() -> {
            final Chunk c = this.generateUndecoratedChunk(cx, cz, key);
            this.mChunk.execute(() -> cf.complete(this.decorateChunk(c, cx, cz, key)));
        });
    }

    /**
     * Attempt to generate a chunk. Queues pregeneration to keep it on a single thread.
     */
    public Chunk generateChunk(int cx, int cz, long key) {
        final CompletableFuture<Chunk> cf = new CompletableFuture<>();
        this.mPregen.execute(() -> cf.complete(this.generateUndecoratedChunk(cx, cz, key)));

        try {
            return this.decorateChunk(cf.get(), cx, cz, key);
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Attempt to generate an undecorated chunk, meant to run async. Does no safety checks, be careful!
     */
    public Chunk generateUndecoratedChunk(int cx, int cz, long key) {
        final Chunk chunk;

        if (this.currentChunkProvider == null) {
            chunk = this.empty;
        } else {
            try {
                chunk = this.localProvider.get().provideChunk(cx, cz);
            } catch (Throwable throwable) {
                CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Exception generating new chunk");
                CrashReportCategory crashreportcategory = crashreport.makeCategory("Chunk to be generated");
                crashreportcategory.addCrashSection("Location", String.format("%d,%d", cx, cz));
                crashreportcategory.addCrashSection("Position hash", key);
                crashreportcategory.addCrashSection("Generator", this.localProvider.get().makeString());
                throw new ReportedException(crashreport);
            }
        }

        return chunk;
    }

    /**
     * Attempt to decorate a generated chunk, meant to run on only one thread at a time (but that doesn't have to be the
     * main thread). Does no safety checks, be careful!
     */
    public Chunk decorateChunk(Chunk chunk, int cx, int cz, long key) {

        this.loadedChunkHashMap.add(key, chunk);
        this.loadedChunks.add(chunk);
        loadingChunks.remove(key);
        chunk.onChunkLoad();
        chunk.populateChunk(this, this, cx, cz);
        return chunk;
    }

    /**
     * Attempt to load a chunk from disk. Does not punt to workers, because it'd block anyways.
     */
    public Chunk loadChunkFromDisk(int cx, int cz, long key) {
        return this.finishChunkFromDisk(this.loadChunkFromDisk(cx, cz), cx, cz, key);
    }

    /**
     * Attempts to load a chunk from disk, returns null if not possible. Meant to run async, and doesn't return a full
     * chunk - run {@link #finishChunkFromDisk(ChunkAndNbt, int, int, long)} to complete it.
     */
    public ChunkAndNbt loadChunkFromDisk(int cx, int cz) {
        try {
            Object[] data = ((AnvilChunkLoader) this.currentChunkLoader).loadChunk__Async(this.worldObj, cx, cz);
            if (data == null) {
                return null;
            }

            final Chunk chunk = (Chunk) data[0];
            final NBTTagCompound nbt = (NBTTagCompound) data[1];
            final ChunkAndNbt cnbt = new ChunkAndNbt(chunk, nbt);

            final NBTTagList entities = nbt.getTagList("Entities", 10);

            if (entities != null) {
                for (int i = 0; i < entities.tagCount(); ++i) {
                    final NBTTagCompound entityTag = entities.getCompoundTagAt(i);
                    final Entity entity = EntityList.createEntityFromNBT(entityTag, this.worldObj);
                    chunk.hasEntities = true;

                    if (entity != null) {
                        chunk.addEntity(entity);
                        Entity riderEntity = entity;

                        for (NBTTagCompound tmpEntityTag = entityTag; tmpEntityTag
                                .hasKey("Riding", 10); tmpEntityTag = tmpEntityTag.getCompoundTag("Riding")) {
                            final Entity riddenEntity = EntityList
                                    .createEntityFromNBT(tmpEntityTag.getCompoundTag("Riding"), this.worldObj);

                            if (riddenEntity != null) {

                                chunk.addEntity(riddenEntity);
                                riderEntity.mountEntity(riddenEntity);
                            }

                            riderEntity = riddenEntity;
                        }
                    }
                }
            }

            final NBTTagList teTags = nbt.getTagList("TileEntities", 10);

            if (teTags != null) {
                for (int i = 0; i < teTags.tagCount(); ++i) {
                    final NBTTagCompound teTag = teTags.getCompoundTagAt(i);
                    TileEntity te = TileEntity.createAndLoadEntity(teTag);

                    if (te != null) {
                        chunk.addTileEntity(te);
                    }
                }
            }

            return cnbt;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Loading a chunk from disk requires some synchronous action, do it here. Despite taking a
     * {@link CompletableFuture}, this is NOT meant to be run on multiple threads at a time - but it doesn't have to be
     * run on the main thread.
     * 
     * @param cnbt If this is null, blocks and generates the chunk instead
     */
    public Chunk finishChunkFromDisk(ChunkAndNbt cnbt, int cx, int cz, long key) {

        if (cnbt == null) {
            return this.generateChunk(cx, cz, key);
        }

        final Chunk chunk = cnbt.getChunk();
        final NBTTagCompound nbt = cnbt.getNbt();

        // Loading tile ticks has to be done synchronously, for now.
        if (nbt.hasKey("TileTicks", 9)) {
            final NBTTagList tileTicks = nbt.getTagList("TileTicks", 10);

            if (tileTicks != null) {
                for (int j1 = 0; j1 < tileTicks.tagCount(); ++j1) {
                    final NBTTagCompound tickTag = tileTicks.getCompoundTagAt(j1);
                    this.worldObj.func_147446_b(
                            tickTag.getInteger("x"),
                            tickTag.getInteger("y"),
                            tickTag.getInteger("z"),
                            Block.getBlockById(tickTag.getInteger("i")),
                            tickTag.getInteger("t"),
                            tickTag.getInteger("p"));
                }
            }
        }

        // This section is very similar to decorateChunk. I won't merge them... for now.
        // Don't call ChunkDataEvent.Load async
        MinecraftForge.EVENT_BUS.post(new ChunkDataEvent.Load(chunk, cnbt.getNbt()));
        chunk.lastSaveTime = this.worldObj.getTotalWorldTime();
        this.loadedChunkHashMap.add(key, chunk);
        this.loadedChunks.add(chunk);
        chunk.onChunkLoad();

        if (this.currentChunkProvider != null) {
            this.localProvider.get().recreateStructures(cx, cz);
        }

        chunk.populateChunk(this, this, cx, cz);

        return chunk;
    }

    @Override
    public List<Chunk> func_152380_a() {
        return this.loadedChunks;
    }

    /**
     * marks chunk for unload by "unload100OldestChunks" if there is no spawn point, or if the center of the chunk is
     * outside 200 blocks (x or z) of the spawn
     */
    @Override
    public void unloadChunksIfNotNearSpawn(int cx, int cz) {
        if (this.worldObj.provider.canRespawnHere()
                && DimensionManager.shouldLoadSpawn(this.worldObj.provider.dimensionId)) {
            final ChunkCoordinates chunkcoordinates = this.worldObj.getSpawnPoint();
            int xBlocksFromSpawn = cx * 16 + 8 - chunkcoordinates.posX;
            int zBlocksFromSpawn = cz * 16 + 8 - chunkcoordinates.posZ;

            if (xBlocksFromSpawn < -128 || xBlocksFromSpawn > 128
                    || zBlocksFromSpawn < -128
                    || zBlocksFromSpawn > 128) {
                this.chunksToUnload.add(ChunkCoordIntPair.chunkXZ2Int(cx, cz));
            }
        } else {
            this.chunksToUnload.add(ChunkCoordIntPair.chunkXZ2Int(cx, cz));
        }
    }

    /**
     * marks all chunks for unload, ignoring those near the spawn
     */
    @Override
    public void unloadAllChunks() {
        for (int i = 0; i < loadedChunks.size(); ++i) {
            final Chunk c = loadedChunks.get(i);
            this.unloadChunksIfNotNearSpawn(c.xPosition, c.zPosition);
        }
    }

    /**
     * Loads or generates the chunk at the chunk location specified. If generation happens, blocks until it's done.
     */
    @Override
    @Deprecated
    public Chunk loadChunk(int cx, int cz) {
        return loadChunk(cx, cz, null);
    }

    /**
     * Loads the chunk specified. If it doesn't exist on disk, it will be generated. The callback passed will be run on
     * loading. Blocks until chunk is ready. This method runs on mServer, and is thus forbidden from decoration.
     */
    @Override
    public Chunk loadChunk(int cx, int cz, Runnable runnable) {

        long key = ChunkCoordIntPair.chunkXZ2Int(cx, cz);
        this.chunksToUnload.remove(key);

        // while (this.loadingChunks.contains(key))
        // LockSupport.parkNanos(1000);

        Chunk chunk = (Chunk) this.loadedChunkHashMap.getValueByKey(key);
        AnvilChunkLoader loader = (AnvilChunkLoader) this.currentChunkLoader;

        // If it's not already loaded...
        if (chunk == null) {
            // If it's already generated...
            if (loader != null && loader.chunkExists(this.worldObj, cx, cz)) {
                // If they have their own callback...
                if (runnable != null) {

                    // Queue the load and finish on worker and main threads; when the latter finishes, run the callback.
                    final CompletableFuture<ChunkAndNbt> cf = new CompletableFuture<>();
                    this.workers.execute(() -> cf.complete(this.loadChunkFromDisk(cx, cz)));
                    this.mChunk.execute(() -> {
                        try {
                            this.finishChunkFromDisk(cf.get(), cx, cz, key);
                        } catch (InterruptedException | ExecutionException e) {
                            throw new RuntimeException(e);
                        }
                        runnable.run();
                    });
                    return null;
                } else {

                    // Punt to the main worker
                    if (this.isMChunk.get()) return this.loadChunkFromDisk(cx, cz, key);

                    final CompletableFuture<Chunk> cf = new CompletableFuture<>();
                    this.mChunk.execute(() -> cf.complete(this.loadChunkFromDisk(cx, cz, key)));
                    try {
                        chunk = cf.get();
                    } catch (InterruptedException | ExecutionException e) {
                        throw new RuntimeException(e);
                    }
                }
            } else {

                // Punt to main generator
                if (this.isMChunk.get()) return this.generateChunk(cx, cz, key);

                final CompletableFuture<Chunk> cf = new CompletableFuture<>();
                this.mChunk.execute(() -> cf.complete(this.generateChunk(cx, cz, key)));
                try {
                    chunk = cf.get();
                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        // If we didn't load the chunk async and have a callback run it now
        if (runnable != null) runnable.run();

        return chunk;
    }

    /**
     * Generate a chunk. Blocks until the chunk is done.
     */
    @Override
    public Chunk originalLoadChunk(int cx, int cz) {
        long key = ChunkCoordIntPair.chunkXZ2Int(cx, cz);
        this.chunksToUnload.remove(key);
        Chunk chunk = (Chunk) this.loadedChunkHashMap.getValueByKey(key);

        if (chunk != null) return chunk;

        if (!loadingChunks.add(key)) {
            cpw.mods.fml.common.FMLLog.bigWarning(
                    "There is an attempt to load a chunk (%d,%d) in dimension %d that is already being loaded. This will cause weird chunk breakages.",
                    cx,
                    cz,
                    worldObj.provider.dimensionId);
        }

        chunk = ForgeChunkManager.fetchDormantChunk(key, this.worldObj);
        if (chunk == null) {
            chunk = this.safeLoadChunk(cx, cz);
        }

        final boolean shouldGen = chunk == null;
        final CompletableFuture<Chunk> cf = new CompletableFuture<>();
        final Chunk finalChunk = chunk; // java why do I have to do this, this isn't even a deep copy
        this.mChunk.execute(
                () -> cf.complete(
                        shouldGen ? this.generateChunk(cx, cz, key) : this.decorateChunk(finalChunk, cx, cz, key)));

        try {
            return cf.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Provides the chunk, if it's been loaded. Otherwise, if {@link World#findingSpawnPoint} or
     * {@link #loadChunkOnProvideRequest}, loads/generates the chunk. If neither is true, returns empty.
     */
    @Override
    public Chunk provideChunk(int cx, int cz) {
        Chunk chunk = (Chunk) this.loadedChunkHashMap.getValueByKey(ChunkCoordIntPair.chunkXZ2Int(cx, cz));
        return chunk != null ? chunk
                : this.worldObj.findingSpawnPoint || this.loadChunkOnProvideRequest ? this.loadChunk(cx, cz)
                        : this.empty;
    }

    /**
     * Attempts to load a chunk from the save files, if not found returns null.
     */
    @Override
    public Chunk safeLoadChunk(int cx, int cz) {
        if (this.currentChunkLoader == null) {
            return null;
        } else {
            final CompletableFuture<Chunk> cf = new CompletableFuture<>();
            this.mChunk
                    .execute(() -> cf.complete(this.loadChunkFromDisk(cx, cz, ChunkCoordIntPair.chunkXZ2Int(cx, cz))));

            try {
                return cf.get();
            } catch (Exception exception) {
                log.error("Couldn't load chunk", exception);
                return null;
            }
        }
    }

    /**
     * used by saveChunks, but catches any exceptions if the save fails.
     */
    private void safeSaveExtraChunkData(Chunk chunk) {
        if (this.currentChunkLoader != null) {
            try {
                this.currentChunkLoader.saveExtraChunkData(this.worldObj, chunk);
            } catch (Exception exception) {
                log.error("Couldn't save entities", exception);
            }
        }
    }

    /**
     * used by saveChunks, but catches any exceptions if the save fails.
     */
    private void safeSaveChunk(Chunk chunk) {
        if (this.currentChunkLoader != null) {
            try {
                chunk.lastSaveTime = this.worldObj.getTotalWorldTime();
                this.currentChunkLoader.saveChunk(this.worldObj, chunk);
            } catch (IOException ioexception) {
                log.error("Couldn't save chunk", ioexception);
            } catch (MinecraftException minecraftexception) {
                log.error("Couldn't save chunk; already in use by another instance of Minecraft?", minecraftexception);
            }
        }
    }

    /**
     * Populates chunk with ores etc etc
     */
    @Override
    public void populate(IChunkProvider backingCP, int cx, int cz) {
        Chunk chunk = this.provideChunk(cx, cz);

        if (!chunk.isTerrainPopulated) {
            chunk.func_150809_p();

            if (this.currentChunkProvider != null) {
                this.localProvider.get().populate(backingCP, cx, cz);
                GameRegistry.generateWorld(cx, cz, worldObj, this.localProvider.get(), backingCP);
                chunk.setChunkModified();
            }
        }
    }

    /**
     * Two modes of operation: if passed true, save all Chunks in one go. If passed false, save up to two chunks. Return
     * true if all chunks have been saved.
     */
    @Override
    public boolean saveChunks(boolean oneshot, IProgressUpdate p_73151_2_) {
        int i = 0;
        ObjectArrayList<Chunk> copiedChunks = new ObjectArrayList<>(this.loadedChunks);

        for (int j = 0; j < copiedChunks.size(); ++j) {
            Chunk chunk = copiedChunks.get(j);

            if (oneshot) {
                this.safeSaveExtraChunkData(chunk);
            }

            if (chunk.needsSaving(oneshot)) {
                this.safeSaveChunk(chunk);
                chunk.isModified = false;
                ++i;

                if (i == 24 && !oneshot) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Save extra data not associated with any Chunk. Not saved during autosave, only during world unload. Currently
     * unimplemented.
     */
    @Override
    public void saveExtraData() {
        if (this.currentChunkLoader != null) {
            this.currentChunkLoader.saveExtraData();
        }
    }

    /**
     * Unloads chunks that are marked to be unloaded. This is not guaranteed to unload every such chunk.
     */
    @Override
    public boolean unloadQueuedChunks() {
        if (!this.worldObj.levelSaving) {
            for (ChunkCoordIntPair forced : this.worldObj.getPersistentChunks().keySet()) {
                this.chunksToUnload.remove(ChunkCoordIntPair.chunkXZ2Int(forced.chunkXPos, forced.chunkZPos));
            }

            for (int i = 0; i < maxUnloadsPerTick; ++i) {
                if (!this.chunksToUnload.isEmpty()) {
                    long key = this.chunksToUnload.iterator().nextLong();
                    Chunk chunk = (Chunk) this.loadedChunkHashMap.getValueByKey(key);

                    if (chunk != null) {
                        chunk.onChunkUnload();
                        this.safeSaveChunk(chunk);
                        this.safeSaveExtraChunkData(chunk);
                        this.loadedChunks.remove(chunk);
                        ForgeChunkManager.putDormantChunk(
                                ChunkCoordIntPair.chunkXZ2Int(chunk.xPosition, chunk.zPosition),
                                chunk);
                        if (loadedChunks.isEmpty() && ForgeChunkManager.getPersistentChunksFor(this.worldObj).isEmpty()
                                && !DimensionManager.shouldLoadSpawn(this.worldObj.provider.dimensionId)) {
                            DimensionManager.unloadWorld(this.worldObj.provider.dimensionId);
                            return currentChunkProvider.unloadQueuedChunks();
                        }
                    }

                    this.chunksToUnload.remove(key);
                    this.loadedChunkHashMap.remove(key);
                }
            }

            if (this.currentChunkLoader != null) {
                this.currentChunkLoader.chunkTick();
            }
        }

        // This will always return a boolean and do no work on the server, as far as I can tell, so it doesn't matter if
        // it's thread-local or not
        return this.currentChunkProvider.unloadQueuedChunks();
    }

    /**
     * Returns if the IChunkProvider supports saving.
     */
    @Override
    public boolean canSave() {
        return !this.worldObj.levelSaving;
    }

    /**
     * Converts the instance data to a readable string.
     */
    @Override
    public String makeString() {
        return "ServerChunkCache: " + this.loadedChunkHashMap.getNumHashElements()
                + " Drop: "
                + this.chunksToUnload.size();
    }

    /**
     * Returns a list of creatures of the specified type that can spawn at the given location.
     */
    @Override
    public List<BiomeGenBase.SpawnListEntry> getPossibleCreatures(EnumCreatureType type, int x, int y, int z) {
        return this.localProvider.get().getPossibleCreatures(type, x, y, z);
    }

    @Override
    public ChunkPosition func_147416_a(World world, String p_147416_2_, int x, int y, int z) {
        return this.localProvider.get().func_147416_a(world, p_147416_2_, x, y, z);
    }

    @Override
    public int getLoadedChunkCount() {
        return this.loadedChunkHashMap.getNumHashElements();
    }

    /**
     * See {@link #doesChunkExist(int, int, long)}
     */
    public boolean doesChunkExist(ChunkCoordIntPair coords) {

        final long key = ChunkCoordIntPair.chunkXZ2Int(coords.chunkXPos, coords.chunkZPos);
        return this.doesChunkExist(coords.chunkXPos, coords.chunkZPos, key);
    }

    /**
     * Given chunk coordinates, returns whether that chunk has been generated already.
     */
    public boolean doesChunkExist(int cx, int cz, long key) {

        if (this.loadedChunkHashMap.containsItem(key)) return true;
        return ((AnvilChunkLoader) this.currentChunkLoader).chunkExists(this.worldObj, cx, cz);
    }

}
