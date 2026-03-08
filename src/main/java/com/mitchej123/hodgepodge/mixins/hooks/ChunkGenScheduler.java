package com.mitchej123.hodgepodge.mixins.hooks;

import java.util.ArrayList;
import java.util.Collection;
import java.util.IdentityHashMap;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S21PacketChunkData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.LongHashMap;
import net.minecraft.util.MathHelper;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.AnvilChunkLoader;
import net.minecraft.world.gen.ChunkProviderServer;

import com.mitchej123.hodgepodge.config.SpeedupsConfig;
import com.mitchej123.hodgepodge.util.ChunkPosUtil;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ByteOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;

public class ChunkGenScheduler {

    private static boolean chunkLoadBlocked = false;
    private static volatile boolean tickingStarted = false;
    private static long serverTickStartNanos;
    private static int blockedLoadCount = 0;

    private static final Int2ObjectOpenHashMap<ChunkGenScheduler> dimensions = new Int2ObjectOpenHashMap<>();
    private final LongOpenHashSet pendingGenSet = new LongOpenHashSet();
    private final Long2IntOpenHashMap chunkRefCount = new Long2IntOpenHashMap();
    private final LongArrayList genWorkList = new LongArrayList();
    private final IdentityHashMap<EntityPlayerMP, LongOpenHashSet> playerChunks = new IdentityHashMap<>();
    private static final byte NONE = 0;
    private static final byte TERRAIN = 1;
    private static final byte POPULATED = 2;
    private static final int PROTECTION_TIMEOUT_TICKS = 600;

    private final Long2ByteOpenHashMap statusMap = new Long2ByteOpenHashMap();
    private final Long2IntOpenHashMap entryTickMap = new Long2IntOpenHashMap();
    private final LongArrayList populationQueue = new LongArrayList();
    private int populationDepth = 0;
    private int terrainCount = 0;
    private final ChunkGenBudget budget = new ChunkGenBudget();
    private final ChunkGenStats stats;
    private final ArrayList<EntityPlayerMP> tempPlayers = new ArrayList<>();
    private final LongArrayList tempLoaded = new LongArrayList();
    private final int dimId;
    private Boolean amortizeOverruns;

    private ChunkGenScheduler(int dimId) {
        this.dimId = dimId;
        this.stats = new ChunkGenStats(dimId, budget);
    }

    public static void onServerTickStart() {
        tickingStarted = true;
        serverTickStartNanos = System.nanoTime();
        chunkLoadBlocked = true;
        blockedLoadCount = 0;
    }

    public static boolean hasTickingStarted() {
        return tickingStarted;
    }

    public static void enableChunkLoads() {
        chunkLoadBlocked = false;
    }

    public static void disableChunkLoads() {
        chunkLoadBlocked = true;
    }

    public static boolean isBlocked() {
        return chunkLoadBlocked;
    }

    public static void incrementBlockedLoadCount() {
        blockedLoadCount++;
    }

    private static int resetBlockedLoadCount() {
        final int c = blockedLoadCount;
        blockedLoadCount = 0;
        return c;
    }

    public static ChunkGenScheduler forDimension(int dimId) {
        return dimensions.computeIfAbsent(dimId, ChunkGenScheduler::new);
    }

    public static ChunkGenScheduler getForDimension(int dimId) {
        return dimensions.get(dimId);
    }

    public ChunkGenStats getStats() {
        return stats;
    }

    public void enqueueForPlayer(EntityPlayerMP player, LongArrayList chunksNeedingGen) {
        if (chunksNeedingGen.isEmpty()) return;
        final LongOpenHashSet playerSet = playerChunks.computeIfAbsent(player, k -> new LongOpenHashSet());
        for (int i = 0; i < chunksNeedingGen.size(); i++) {
            final long packed = chunksNeedingGen.getLong(i);
            if (playerSet.add(packed)) {
                pendingGenSet.add(packed);
                chunkRefCount.merge(packed, 1, Integer::sum);
            }
        }
    }

    /** Removes player and any chunks no longer wanted by other players. */
    public void removePlayer(EntityPlayerMP player) {
        final LongOpenHashSet removed = playerChunks.remove(player);
        if (removed == null || removed.isEmpty()) return;
        for (long packed : removed) {
            final int remaining = chunkRefCount.merge(packed, -1, Integer::sum);
            if (remaining <= 0) {
                chunkRefCount.remove(packed);
                pendingGenSet.remove(packed);
            }
        }
    }

    public int getPopulationDepth() {
        return populationDepth;
    }

    public void incrementPopulationDepth() {
        populationDepth++;
    }

    public void decrementPopulationDepth() {
        populationDepth--;
    }

    public void deferChunkPopulation(Chunk chunk, int x, int z) {
        trackTerrain(chunk, x, z);
        stats.incrementDeferred();
    }

    public void trackIfUnpopulated(Chunk chunk, int x, int z) {
        stats.incrementDirect();
        trackTerrain(chunk, x, z);
    }

    private void trackTerrain(Chunk chunk, int x, int z) {
        if (!chunk.isTerrainPopulated) {
            final long key = ChunkPosUtil.toLong(x, z);
            if (!statusMap.containsKey(key)) {
                statusMap.put(key, TERRAIN);
                terrainCount++;
                entryTickMap.put(key, MinecraftServer.getServer().getTickCounter());
                populationQueue.add(key);
            }
        }
    }

    public boolean shouldProtectFromUnload(long key) {
        final int currentTick = MinecraftServer.getServer().getTickCounter();
        if (isProtectedTerrain(key, currentTick)) return true;
        final int cx = ChunkPosUtil.getPackedX(key);
        final int cz = ChunkPosUtil.getPackedZ(key);
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                if (dx == 0 && dz == 0) continue;
                if (isProtectedTerrain(ChunkPosUtil.toLong(cx + dx, cz + dz), currentTick)) return true;
            }
        }
        return false;
    }

    private boolean isProtectedTerrain(long key, int currentTick) {
        if (statusMap.get(key) != TERRAIN) return false;
        return currentTick - entryTickMap.getOrDefault(key, Integer.MIN_VALUE) < PROTECTION_TIMEOUT_TICKS;
    }

    public void onChunkUnload(long key) {
        removeStatus(key);
    }

    private void removeStatus(long key) {
        if (statusMap.remove(key) == TERRAIN) {
            terrainCount--;
        }
        entryTickMap.remove(key);
    }

    @FunctionalInterface
    public interface ChunkLoadCallback {

        void loadChunk(int cx, int cz, Collection<EntityPlayerMP> players);
    }

    /** Tick processing: fills → population → terrain gen → force-aged, all within the configured budget. */
    public void processTick(WorldServer world, ChunkProviderServer cps, int viewRadius, AnvilChunkLoader acl,
            ChunkLoadCallback loadCallback) {

        final long configBudgetNanos = (long) SpeedupsConfig.chunkGenBudgetMs * 1_000_000L;
        if (pendingGenSet.isEmpty() && populationQueue.isEmpty()) {
            stats.recordTick(0, 0, 0, resetBlockedLoadCount(), 0, 0, 0, 0, 0, configBudgetNanos, false);
            return;
        }

        final long now = System.nanoTime();
        final long elapsed = now - serverTickStartNanos;
        final long tickBudgetNanos = configBudgetNanos > 0 ? Math.min(configBudgetNanos, 50_000_000L - elapsed) : 0;

        final boolean inDebt;
        if (shouldAmortize() && tickBudgetNanos > 0 && budget.hasDebt()) {
            budget.payDown(tickBudgetNanos);
            inDebt = true;
        } else {
            inDebt = false;
        }

        int totalTerrainGen = 0;
        int popProcessed = 0;
        long terrainNanos = 0;
        long popNanos = 0;
        long fillNanos = 0;

        final int viewDist = MinecraftServer.getServer().getConfigurationManager().getViewDistance();

        enableChunkLoads();
        final long phaseStart = System.nanoTime();
        try {
            pruneAndSort(world, cps, acl, viewRadius, loadCallback);

            if (!inDebt && tickBudgetNanos > 0) {
                if (!populationQueue.isEmpty()) {
                    final long fillStart = System.nanoTime();
                    processFills(tickBudgetNanos, world, cps, viewDist);
                    fillNanos = System.nanoTime() - fillStart;
                }

                if (!populationQueue.isEmpty()) {
                    final long popBudget = Math.max(0, tickBudgetNanos - fillNanos);
                    final long popStart = System.nanoTime();
                    popProcessed = processPopulationQueue(popBudget, world, cps, viewDist);
                    popNanos = System.nanoTime() - popStart;
                }

                if (!pendingGenSet.isEmpty()) {
                    final long terrainBudget = Math.max(0, tickBudgetNanos - fillNanos - popNanos);
                    final long terrainStart = System.nanoTime();
                    totalTerrainGen = generateTerrain(world, terrainBudget, terrainStart, loadCallback);
                    terrainNanos = System.nanoTime() - terrainStart;
                }
            }

            if (!populationQueue.isEmpty() && SpeedupsConfig.forcePopulateAgeTicks > 0) {
                forceProcessAged(world, cps, viewDist);
            }
        } finally {
            disableChunkLoads();
        }

        final long actualNanos = System.nanoTime() - phaseStart;

        if (shouldAmortize() && !inDebt && tickBudgetNanos > 0) {
            budget.recordOverrun(tickBudgetNanos, actualNanos);
        }

        stats.recordTick(
                pendingGenSet.size(),
                populationQueue.size(),
                getTerrainStatusCount(),
                resetBlockedLoadCount(),
                totalTerrainGen,
                terrainNanos,
                popProcessed,
                popNanos,
                actualNanos,
                tickBudgetNanos,
                inDebt);
    }

    private int generateTerrain(WorldServer world, long terrainBudgetNanos, long terrainStart,
            ChunkLoadCallback loadCallback) {
        if (pendingGenSet.isEmpty()) return 0;

        final int maxTotal = SpeedupsConfig.maxChunkGenPerPlayerPerTick * Math.max(1, playerChunks.size());
        int totalGen = 0;

        while (!genWorkList.isEmpty() && totalGen < maxTotal) {
            // At least one chunk always processes to prevent permanent stall
            if (totalGen > 0) {
                final long remaining = terrainBudgetNanos - (System.nanoTime() - terrainStart);
                if (remaining <= 0 || !budget.canAffordTerrainGen(remaining)) break;
            }

            final int idx = genWorkList.size() - 1;
            final long packed = genWorkList.getLong(idx);
            genWorkList.removeLong(idx);

            if (!pendingGenSet.remove(packed)) continue;

            final int cx = ChunkPosUtil.getPackedX(packed);
            final int cz = ChunkPosUtil.getPackedZ(packed);

            tempPlayers.clear();
            for (var entry : playerChunks.entrySet()) {
                if (entry.getValue().remove(packed)) {
                    tempPlayers.add(entry.getKey());
                }
            }
            chunkRefCount.remove(packed);

            final long before = System.nanoTime();
            loadCallback.loadChunk(cx, cz, tempPlayers);
            budget.recordTerrainGen(System.nanoTime() - before);
            totalGen++;
        }

        playerChunks.values().removeIf(LongOpenHashSet::isEmpty);

        return totalGen;
    }

    /** Fill missing neighbors for TERRAIN-status chunks. First fill per tick is guaranteed to prevent stall. */
    private int processFills(long budgetNanos, WorldServer world, ChunkProviderServer cps, int viewDist) {
        if (populationQueue.isEmpty()) return 0;
        final LongHashMap loadedChunkHashMap = cps.loadedChunkHashMap;
        final long startNanos = System.nanoTime();
        boolean guaranteedFillUsed = false;
        int totalFills = 0;
        int write = 0;

        for (int i = 0; i < populationQueue.size(); i++) {
            final long key = populationQueue.getLong(i);
            final byte status = statusMap.get(key);

            if (status != TERRAIN) {
                populationQueue.set(write++, key);
                continue;
            }

            final int cx = ChunkPosUtil.getPackedX(key);
            final int cz = ChunkPosUtil.getPackedZ(key);
            final Chunk chunk = (Chunk) loadedChunkHashMap.getValueByKey(key);
            if (chunk == null || chunk.isTerrainPopulated) {
                removeStatus(key);
                continue;
            }

            if (has3x3Terrain(loadedChunkHashMap, cx, cz)) {
                populationQueue.set(write++, key);
                continue;
            }

            if (!isWithinChunkRange(world, cx, cz, viewDist + 1)) {
                removeStatus(key);
                continue;
            }

            final int filled = fillMissingNeighbors(
                    cps,
                    loadedChunkHashMap,
                    world,
                    cx,
                    cz,
                    budgetNanos,
                    startNanos,
                    !guaranteedFillUsed);
            if (filled > 0) guaranteedFillUsed = true;
            totalFills += filled;
            stats.addNeighborFills(filled);
            if (filled == 0) stats.incrementStuck();
            populationQueue.set(write++, key);
        }

        if (write < populationQueue.size()) {
            populationQueue.removeElements(write, populationQueue.size());
        }
        return totalFills;
    }

    /** Populate chunks with all 3x3 neighbors loaded. Others are kept for next tick's fill pass. */
    private int processPopulationQueue(long budgetNanos, WorldServer world, ChunkProviderServer cps, int viewDist) {
        if (populationQueue.isEmpty()) return 0;
        final LongHashMap loadedChunkHashMap = cps.loadedChunkHashMap;
        int processed = 0;
        final long startNanos = System.nanoTime();
        populationDepth++;
        try {
            int write = 0;
            int i;
            for (i = 0; i < populationQueue.size(); i++) {
                final long key = populationQueue.getLong(i);
                final byte status = statusMap.get(key);

                if (status == POPULATED) {
                    final int cx = ChunkPosUtil.getPackedX(key);
                    final int cz = ChunkPosUtil.getPackedZ(key);
                    if (!isWithinChunkRange(world, cx, cz, viewDist)) {
                        removeStatus(key);
                        continue;
                    }
                    if (queueResend(world, cps, cx, cz, viewDist)) {
                        removeStatus(key);
                    } else {
                        populationQueue.set(write++, key);
                        stats.incrementResendRetry();
                    }
                    continue;
                }

                if (status != TERRAIN) {
                    removeStatus(key);
                    continue;
                }

                final int cx = ChunkPosUtil.getPackedX(key);
                final int cz = ChunkPosUtil.getPackedZ(key);
                final Chunk chunk = (Chunk) loadedChunkHashMap.getValueByKey(key);
                if (chunk == null || chunk.isTerrainPopulated) {
                    removeStatus(key);
                    continue;
                }

                if (!has3x3Terrain(loadedChunkHashMap, cx, cz)) {
                    populationQueue.set(write++, key);
                    continue;
                }

                // At least one always processes to prevent stall
                if (processed > 0) {
                    final long remaining = budgetNanos - (System.nanoTime() - startNanos);
                    if (remaining <= 0 || !budget.canAffordPopulate(remaining)) {
                        populationQueue.set(write++, key);
                        for (int j = i + 1; j < populationQueue.size(); j++) {
                            populationQueue.set(write++, populationQueue.getLong(j));
                        }
                        break;
                    }
                }

                final long before = System.nanoTime();
                cps.populate(cps, cx, cz);
                budget.recordPopulate(System.nanoTime() - before);

                if (queueResend(world, cps, cx, cz, viewDist)) {
                    removeStatus(key);
                } else {
                    statusMap.put(key, POPULATED);
                    terrainCount--;
                    populationQueue.set(write++, key);
                }
                processed++;
            }
            if (write < populationQueue.size()) {
                populationQueue.removeElements(write, populationQueue.size());
            }
        } finally {
            populationDepth--;
        }
        return processed;
    }

    private int fillMissingNeighbors(ChunkProviderServer cps, LongHashMap loadedChunkHashMap, WorldServer world, int cx,
            int cz, long budgetNanos, long startNanos, boolean guaranteeFirst) {
        int filled = 0;
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                if (dx == 0 && dz == 0) continue;
                final int nx = cx + dx;
                final int nz = cz + dz;
                final long nk = ChunkPosUtil.toLong(nx, nz);
                if (loadedChunkHashMap.getValueByKey(nk) != null) continue;

                if (!(guaranteeFirst && filled == 0)) {
                    final long remaining = budgetNanos - (System.nanoTime() - startNanos);
                    if (remaining <= 0) return filled;
                }

                final long before = System.nanoTime();
                cps.provideChunk(nx, nz);
                budget.recordTerrainGen(System.nanoTime() - before);
                filled++;
            }
        }
        return filled;
    }

    private static boolean isWithinChunkRange(WorldServer world, int cx, int cz, int limit) {
        for (Object obj : world.playerEntities) {
            final EntityPlayer ep = (EntityPlayer) obj;
            final int px = MathHelper.floor_double(ep.posX) >> 4;
            final int pz = MathHelper.floor_double(ep.posZ) >> 4;
            if (Math.abs(cx - px) <= limit && Math.abs(cz - pz) <= limit) return true;
        }
        return false;
    }

    private static boolean has3x3Terrain(LongHashMap loadedChunkHashMap, int cx, int cz) {
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                if (dx == 0 && dz == 0) continue;
                if (loadedChunkHashMap.getValueByKey(ChunkPosUtil.toLong(cx + dx, cz + dz)) == null) return false;
            }
        }
        return true;
    }

    private static boolean queueResend(WorldServer world, ChunkProviderServer cps, int cx, int cz, int viewRadius) {
        final Chunk chunk = (Chunk) cps.loadedChunkHashMap.getValueByKey(ChunkPosUtil.toLong(cx, cz));
        if (chunk == null) return false;
        boolean matched = false;
        for (Object obj : world.playerEntities) {
            if (!(obj instanceof EntityPlayerMP player)) continue;
            final int px = MathHelper.floor_double(player.posX) >> 4;
            final int pz = MathHelper.floor_double(player.posZ) >> 4;
            if (Math.abs(cx - px) <= viewRadius && Math.abs(cz - pz) <= viewRadius) {
                player.playerNetServerHandler.sendPacket(new S21PacketChunkData(chunk, true, 0xFFFF));
                for (Object teObj : chunk.chunkTileEntityMap.values()) {
                    if (teObj instanceof TileEntity te) {
                        final Packet pkt = te.getDescriptionPacket();
                        if (pkt != null) player.playerNetServerHandler.sendPacket(pkt);
                    }
                }
                matched = true;
            }
        }
        return matched;
    }

    private static double minPlayerDistSq(WorldServer world, int cx, int cz, int viewDist) {
        double minDistSq = -1;
        for (Object obj : world.playerEntities) {
            final EntityPlayer ep = (EntityPlayer) obj;
            final int px = MathHelper.floor_double(ep.posX) >> 4;
            final int pz = MathHelper.floor_double(ep.posZ) >> 4;
            final int dx = Math.abs(cx - px);
            final int dz = Math.abs(cz - pz);
            if (dx <= viewDist && dz <= viewDist) {
                final double distSq = (double) dx * dx + (double) dz * dz;
                if (minDistSq < 0 || distSq < minDistSq) {
                    minDistSq = distSq;
                }
            }
        }
        return minDistSq;
    }

    /** Force-populate the closest aged hole (1 per tick max, bypasses budget). */
    private void forceProcessAged(WorldServer world, ChunkProviderServer cps, int viewDist) {
        final int threshold = SpeedupsConfig.forcePopulateAgeTicks;
        if (threshold <= 0) return;

        final int currentTick = MinecraftServer.getServer().getTickCounter();
        final LongHashMap loadedChunkHashMap = cps.loadedChunkHashMap;

        long bestKey = Long.MIN_VALUE;
        double bestDistSq = Double.MAX_VALUE;
        int bestAge = 0;

        for (int i = 0; i < populationQueue.size(); i++) {
            final long key = populationQueue.getLong(i);
            if (statusMap.get(key) != TERRAIN) continue;

            final int age = currentTick - entryTickMap.getOrDefault(key, currentTick);
            if (age < threshold) continue;

            final int cx = ChunkPosUtil.getPackedX(key);
            final int cz = ChunkPosUtil.getPackedZ(key);

            final double minDist = minPlayerDistSq(world, cx, cz, viewDist);
            if (minDist < 0) continue;

            if (minDist < bestDistSq) {
                bestKey = key;
                bestDistSq = minDist;
                bestAge = age;
            }
        }

        if (bestKey == Long.MIN_VALUE) return;

        final int cx = ChunkPosUtil.getPackedX(bestKey);
        final int cz = ChunkPosUtil.getPackedZ(bestKey);

        final Chunk chunk = (Chunk) loadedChunkHashMap.getValueByKey(bestKey);
        if (chunk == null || chunk.isTerrainPopulated) {
            removeStatus(bestKey);
            return;
        }

        int neighborsFilled = 0;
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                if (dx == 0 && dz == 0) continue;
                final int nx = cx + dx;
                final int nz = cz + dz;
                final long nk = ChunkPosUtil.toLong(nx, nz);
                if (loadedChunkHashMap.getValueByKey(nk) == null) {
                    cps.provideChunk(nx, nz);
                    neighborsFilled++;
                }
            }
        }

        if (neighborsFilled > 2) {
            // Too many neighbors needed — defer population to next tick when they'll be loaded
            ThrottleStats.logForceProcess(dimId, cx, cz, bestAge, neighborsFilled);
            return;
        }

        populationDepth++;
        try {
            cps.populate(cps, cx, cz);
        } finally {
            populationDepth--;
        }

        queueResend(world, cps, cx, cz, viewDist);
        removeStatus(bestKey);

        ThrottleStats.logForceProcess(dimId, cx, cz, bestAge, neighborsFilled);
    }

    private void pruneAndSort(WorldServer world, ChunkProviderServer cps, AnvilChunkLoader acl, int viewRadius,
            ChunkLoadCallback loadCallback) {
        final var playerIter = playerChunks.entrySet().iterator();
        while (playerIter.hasNext()) {
            final var entry = playerIter.next();
            final EntityPlayerMP player = entry.getKey();
            final LongOpenHashSet chunks = entry.getValue();
            final int cx = MathHelper.floor_double(player.posX) >> 4;
            final int cz = MathHelper.floor_double(player.posZ) >> 4;
            chunks.removeIf(
                    packed -> Math.abs(ChunkPosUtil.getPackedX(packed) - cx) > viewRadius
                            || Math.abs(ChunkPosUtil.getPackedZ(packed) - cz) > viewRadius);
            if (chunks.isEmpty()) playerIter.remove();
        }

        pendingGenSet.clear();
        chunkRefCount.clear();
        for (var chunks : playerChunks.values()) {
            pendingGenSet.addAll(chunks);
            for (long packed : chunks) {
                chunkRefCount.merge(packed, 1, Integer::sum);
            }
        }

        // Load chunks now available in memory/on disk
        if (!pendingGenSet.isEmpty()) {
            tempLoaded.clear();
            for (long packed : pendingGenSet) {
                final int x = ChunkPosUtil.getPackedX(packed);
                final int z = ChunkPosUtil.getPackedZ(packed);
                if (cps.chunkExists(x, z) || (acl != null && acl.chunkExists(world, x, z))) {
                    tempLoaded.add(packed);
                }
            }
            for (int i = 0; i < tempLoaded.size(); i++) {
                final long packed = tempLoaded.getLong(i);
                final int x = ChunkPosUtil.getPackedX(packed);
                final int z = ChunkPosUtil.getPackedZ(packed);
                tempPlayers.clear();
                for (var entry : playerChunks.entrySet()) {
                    if (entry.getValue().remove(packed)) {
                        tempPlayers.add(entry.getKey());
                    }
                }
                pendingGenSet.remove(packed);
                chunkRefCount.remove(packed);
                loadCallback.loadChunk(x, z, tempPlayers);
            }
        }

        rebuildGenWorkList(world);
    }

    private void rebuildGenWorkList(WorldServer world) {
        genWorkList.clear();
        genWorkList.addAll(pendingGenSet);

        if (genWorkList.isEmpty()) return;

        // Descending: closest chunks last for tail-pop
        genWorkList.sort((a, b) -> {
            final double aScore = minPlayerScore(a, world);
            final double bScore = minPlayerScore(b, world);
            return Double.compare(bScore, aScore);
        });
    }

    /** distSq with forward-bias toward player movement direction. Lower = closer / more ahead. */
    private static double minPlayerScore(long packed, WorldServer world) {
        final int cx = ChunkPosUtil.getPackedX(packed);
        final int cz = ChunkPosUtil.getPackedZ(packed);
        double minScore = Double.MAX_VALUE;
        for (Object obj : world.playerEntities) {
            final EntityPlayer player = (EntityPlayer) obj;
            final int px = MathHelper.floor_double(player.posX) >> 4;
            final int pz = MathHelper.floor_double(player.posZ) >> 4;
            final double motionX = player.posX - player.prevPosX;
            final double motionZ = player.posZ - player.prevPosZ;
            final int dx = cx - px;
            final int dz = cz - pz;
            final double score = dx * dx + dz * dz - (dx * motionX + dz * motionZ) * 2.0;
            if (score < minScore) minScore = score;
        }
        return minScore;
    }

    private int getTerrainStatusCount() {
        return terrainCount;
    }

    private boolean shouldAmortize() {
        if (amortizeOverruns == null) {
            amortizeOverruns = switch (SpeedupsConfig.amortizeChunkGenOverruns) {
                case Always -> true;
                case Never -> false;
                case DedicatedServerOnly -> MinecraftServer.getServer().isDedicatedServer();
            };
        }
        return amortizeOverruns;
    }
}
