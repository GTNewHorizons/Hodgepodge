package com.mitchej123.hodgepodge.common;

import static com.mitchej123.hodgepodge.util.ChunkPosUtil.getPackedX;
import static com.mitchej123.hodgepodge.util.ChunkPosUtil.getPackedZ;
import static com.mitchej123.hodgepodge.util.ChunkPosUtil.toLong;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.chunk.storage.AnvilChunkLoader;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraftforge.common.DimensionManager;

public class CPSPregen {
    private static final Int2ObjectOpenHashMap<CPSPregen> PREGENERATORS = new Int2ObjectOpenHashMap<>();

    private final WorldProvider provider;
    private final AnvilChunkLoader chunkLoader;
    private final ChunkProviderServer cps;
    private final ExecutorService executor = Executors.newFixedThreadPool(4);
    private final ObjectArrayList<IChunkProvider> generators = new ObjectArrayList<>();
    private final Semaphore lock = new Semaphore(1);
    private final ObjectArrayList<CompletableFuture<CGTask>> tasks = new ObjectArrayList<>();

    public CPSPregen(WorldProvider provider, AnvilChunkLoader chunkLoader, ChunkProviderServer cps) {
        this.provider = provider;
        this.chunkLoader = chunkLoader;
        this.cps = cps;
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
                if (chunkLoader.chunkExists(null, x, z)) continue;
                while (!lock.tryAcquire()) { Thread.yield(); }
                var gen = generators.isEmpty() ? provider.createChunkGenerator() : generators.pop();
                lock.release();
                tasks.add(submitChunk(x, z, gen));
            }
        }

        // Decorate and load the chunks - hope you have an aggressive unloader!
        int numGenerated = 0;
        for (int i = 0; i < tasks.size(); i++) {
            var task = tasks.get(i);
            try {
                var result = task.get();

                if (cps.loadedChunkHashMap.containsItem(result.pos)) continue;

                cps.loadedChunkHashMap.add(result.pos, result.chunk);
                cps.loadedChunks.add(result.chunk);
                result.chunk.onChunkLoad();
                result.chunk.populateChunk(cps, cps, getPackedX(result.pos), getPackedZ(result.pos));
                numGenerated++;
            } catch (InterruptedException ignored) {
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        }

        return numGenerated;
    }

    private CompletableFuture<CGTask> submitChunk(int cx, int cz, IChunkProvider gen) {
        var ret = new CompletableFuture<CGTask>();
        executor.submit(() -> {
            var undec = gen.provideChunk(cx, cz);
            ret.complete(new CGTask(undec, toLong(cx, cz)));
            while (!lock.tryAcquire()) { Thread.yield(); }
            generators.add(gen);
            lock.release();
        });
        return ret;
    }

    private static class CGTask {
        private final Chunk chunk;
        private final long pos;

        private CGTask(Chunk c, long pos) {
            chunk = c;
            this.pos = pos;
        }
    }
}
