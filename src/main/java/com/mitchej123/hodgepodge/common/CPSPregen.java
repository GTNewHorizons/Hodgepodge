package com.mitchej123.hodgepodge.common;

import static com.mitchej123.hodgepodge.Common.log;
import static com.mitchej123.hodgepodge.util.ChunkPosUtil.toLong;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.chunk.storage.AnvilChunkLoader;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraftforge.common.DimensionManager;

public class CPSPregen {
    private static final Int2ObjectOpenHashMap<CPSPregen> PREGENERATORS = new Int2ObjectOpenHashMap<>();
    private static final int N_WORKERS = 4;
    private static final int INTERVAL = 25;

    private final WorldProvider provider;
    private final AnvilChunkLoader chunkLoader;
    private final ChunkProviderServer cps;
    private final ExecutorService[] workers = new ExecutorService[N_WORKERS];
    private final IChunkProvider[] chunkProviders = new IChunkProvider[N_WORKERS];
    private final ExecutorService executor = Executors.newFixedThreadPool(4);
    //private final ObjectArrayList<IChunkProvider> generators = new ObjectArrayList<>();
    //private final Semaphore lock = new Semaphore(1);
    private final ObjectArrayList<CGFuture> tasks = new ObjectArrayList<>();

    public CPSPregen(WorldProvider provider, AnvilChunkLoader chunkLoader, ChunkProviderServer cps) {
        this.provider = provider;
        this.chunkLoader = chunkLoader;
        this.cps = cps;

        for (int i = 0; i < N_WORKERS; i++) {
            workers[i] = Executors.newSingleThreadExecutor();
        }
    }

    public static CPSPregen getPregenerator(int dimID) {
        var ret = PREGENERATORS.get(dimID);
        if (ret != null) return ret;

        var world = DimensionManager.getWorld(dimID);
        var cps = world.theChunkProviderServer;
        ret = new CPSPregen(world.provider, (AnvilChunkLoader) cps.currentChunkLoader, cps);
        PREGENERATORS.put(dimID, ret);
        return ret;
    }

    /**
     * Generates the given chunks and blocks until finished. Call this at the end of a tick, after other work is done,
     * and chunks aren't in the process of loading.
     * @param cxl The lowest chunk X coordinate of the queued chunks.
     * @param czl The lowest chunk Z coordinate of the queued chunks.
     * @param cxh The highest chunk X coordinate of the queued chunks.
     * @param czh The highest chunk Z coordinate of the queued chunks.
     */
    public int generateChunksBlocking(int cxl, int czl, int cxh, int czh) {
        for (int x = cxl; x <= cxh; x++) {
            for (int z = czl; z <= czh; z++) {
                if (cps.chunkExists(x, z) || chunkLoader.chunkExists(null, x, z)) continue;

                final int cx = x;
                final int cz = z;
                final var t = new CGFuture(x, z);
                final var gen = provider.createChunkGenerator();
                tasks.add(t);
                executor.submit(() -> t.complete(gen.provideChunk(cx, cz)));
            }
        }

        // Decorate and load the chunks - hope you have an aggressive unloader!
        int numGenerated = 0;
        boolean skip = false;
        var len = tasks.size();
        for (int i = 0; i < len; i++) {
            var task = tasks.pop();
            try {
                numGenerated++;
                //if (numGenerated % INTERVAL == 0) {
                    log.info("Processed {} chunks", numGenerated);
                //}

                if (cps.chunkExists(task.cx, task.cz)) continue;

                var result = task.get(/** /5, TimeUnit.MILLISECONDS/**/);
                cps.loadedChunkHashMap.add(task.pos, result);
                cps.loadedChunks.add(result);
                result.onChunkLoad();
                result.populateChunk(cps, cps, task.cx, task.cz);
            } catch (InterruptedException ignored) {
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }/** / catch (TimeoutException e) {
                log.warn("Timed out while waiting for chunk generation - skipping chunk!");
                skip = true;
            }//*/
        }

        if (skip) {
            log.warn("Going around again!");
            numGenerated += generateChunksBlocking(cxl, czl, cxh, czh);
        }
        return numGenerated;
    }

    private static class CGFuture extends CompletableFuture<Chunk> {
        private final int cx;
        private final int cz;
        private final long pos;

        private CGFuture(int cx, int cz) {
            this.cx = cx;
            this.cz = cz;
            this.pos = toLong(cx, cz);
        }
    }
}
