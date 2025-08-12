package com.mitchej123.hodgepodge;

import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.WeakHashMap;
import java.util.function.BiPredicate;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.NextTickListEntry;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.ForgeChunkManager;

import com.mitchej123.hodgepodge.config.FixesConfig;
import com.mitchej123.hodgepodge.config.TweaksConfig;
import com.mitchej123.hodgepodge.util.ChunkPosUtil;

import cpw.mods.fml.common.FMLLog;
import it.unimi.dsi.fastutil.booleans.BooleanArrayList;
import it.unimi.dsi.fastutil.longs.Long2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ByteOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectAVLTreeSet;

public class SimulationDistanceHelper {

    /**
     * Mark a chunk as no to be simulated, or reset that state. Not thread safe!
     */
    public static void preventChunkSimulation(World world, long packedChunkPos, boolean prevent) {
        if (!FixesConfig.addSimulationDistance) {
            return;
        }
        ISimulationDistanceWorld mixin = (ISimulationDistanceWorld) world;
        mixin.hodgepodge$preventChunkSimulation(packedChunkPos, prevent);
    }

    public static int getSimulationDistance() {
        return TweaksConfig.simulationDistance;
    }

    public static void setSimulationDistance(int distance) {
        TweaksConfig.simulationDistance = distance;
    }

    public SimulationDistanceHelper(World world) {
        this.worldRef = new WeakReference<>(world);
        isServer = world instanceof WorldServer;
    }

    /**
     * The current world
     */
    private final WeakReference<World> worldRef;

    /**
     * True if we have a WorldServer
     */
    private final boolean isServer;

    /**
     * Chunks that should not be ticked
     */
    private final Long2ByteOpenHashMap noTickChunks = new Long2ByteOpenHashMap();

    /**
     * Changes to the chunks that should not be ticked. Needed since we only update the real map once a tick. Reason is
     * that forced chunks are also cached and updated once a tick, we want both in sync.
     */
    private final LongBooleanArrayList noTickChunksChanges = new LongBooleanArrayList();

    /**
     * Cache of forced chunks. Profiler says that is faster.
     */
    private final LongOpenHashSet forcedChunksMap = new LongOpenHashSet();

    /**
     * Cache to quickly check if a chunk should be processed or not. Profiler says this is faster.
     */
    private final Long2BooleanOpenHashMap cacheShouldProcessTick = new Long2BooleanOpenHashMap();

    /**
     * Old player positions to see if they moved.
     */
    private final Map<EntityPlayer, ChunkCoordIntPair> playerPosOldMap = new WeakHashMap<>();

    /**
     * All ticks that are currently considered.
     */
    private final ObjectAVLTreeSet<NextTickListEntry> pendingTickCandidates = new ObjectAVLTreeSet<>();

    /**
     * Map of ticks to chunk, duplicating pendingTickListEntriesTreeSet for quicker lookup.
     */
    private final Long2ObjectMap<HashSet<NextTickListEntry>> chunkTickMap = new Long2ObjectOpenHashMap<>();

    /**
     * TreeSet pendingTickListEntriesTreeSet from WorldServer
     */
    private TreeSet<NextTickListEntry> pendingTickListEntriesTreeSet;
    /**
     * HashSet pendingTickListEntriesHashSet from WorldServer
     */
    private Set<NextTickListEntry> pendingTickListEntriesHashSet;
    /**
     * Check if chunk exists in WorldServer
     */
    private BiPredicate<Integer, Integer> chunkExists;

    /**
     * Simulation distance from last tick, to detect if it has changed.
     */
    private int simulationDistanceOld;

    public void preventChunkSimulation(long packedChunkPos, boolean prevent) {
        noTickChunksChanges.add(packedChunkPos, prevent);
    }

    private boolean closeToPlayer(long packedChunkPos) {
        final int cx = ChunkPosUtil.getPackedX(packedChunkPos);
        final int cz = ChunkPosUtil.getPackedZ(packedChunkPos);

        World world = worldRef.get();
        if (world == null) {
            return false;
        }

        int simulationDistance = getSimulationDistance();
        for (EntityPlayer player : world.playerEntities) {
            if (player.getEntityWorld() != world) {
                continue;
            }
            int playerCX = (int) player.posX >> 4;
            int playerCZ = (int) player.posZ >> 4;
            if (Math.abs(playerCX - cx) <= simulationDistance && Math.abs(playerCZ - cz) <= simulationDistance) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if a chunk should get processed
     */
    public boolean shouldProcessTick(int x, int z) {
        long key = ChunkPosUtil.toLong(x, z);
        return cacheShouldProcessTick.computeIfAbsent(key, k -> {
            if (closeToPlayer(k)) return true;
            if (noTickChunks.containsKey(k)) return false;
            return forcedChunksMap.contains(k);
        });
    }

    private LongOpenHashSet getPlayerChunksForPos(ChunkCoordIntPair pos) {
        int simulationDistance = getSimulationDistance();
        int diameter = simulationDistance * 2 + 1;
        LongOpenHashSet chunks = new LongOpenHashSet(diameter * diameter);
        for (int x = -simulationDistance; x <= simulationDistance; x++) {
            for (int z = -simulationDistance; z <= simulationDistance; z++) {
                chunks.add(ChunkCoordIntPair.chunkXZ2Int(pos.chunkXPos + x, pos.chunkZPos + z));
            }
        }
        return chunks;
    }

    private void mergeNoTickChunkChanges() {
        for (int i = 0; i < noTickChunksChanges.size(); i++) {
            long key = noTickChunksChanges.getLong(i);
            boolean prevent = noTickChunksChanges.getBoolean(i);
            byte value = noTickChunks.getOrDefault(key, (byte) 0);
            value += (byte) (prevent ? 1 : -1);
            if (value > 0) {
                noTickChunks.put(key, value);
            } else {
                noTickChunks.remove(key);
            }
        }
        noTickChunksChanges.clear();
    }

    private void checkForAddedChunks(LongOpenHashSet forcedChunksOld) {
        World world = worldRef.get();
        if (world == null) {
            return;
        }

        LongOpenHashSet added = new LongOpenHashSet();
        int simulationDistance = getSimulationDistance();

        // New forced chunks?
        LongIterator iterator = forcedChunksMap.iterator();
        while (iterator.hasNext()) {
            long chunk = iterator.nextLong();
            if (!forcedChunksOld.contains(chunk)) {
                added.add(chunk);
            }
        }

        // New players, or moved players?
        for (EntityPlayer player : world.playerEntities) {
            if (player.getEntityWorld() != world) {
                continue;
            }
            ChunkCoordIntPair playerPos = new ChunkCoordIntPair((int) player.posX >> 4, (int) player.posZ >> 4);
            ChunkCoordIntPair playerPosOld = playerPosOldMap.getOrDefault(player, null);
            if (playerPosOld == null || simulationDistance != simulationDistanceOld) {
                LongOpenHashSet chunksNewPos = getPlayerChunksForPos(playerPos);
                added.addAll(chunksNewPos);
            } else if (!playerPos.equals(playerPosOld)) {
                LongOpenHashSet chunksNewPos = getPlayerChunksForPos(playerPos);
                LongOpenHashSet chunksOldPos = getPlayerChunksForPos(playerPosOld);
                iterator = chunksNewPos.iterator();
                while (iterator.hasNext()) {
                    long pos = iterator.nextLong();
                    if (!chunksOldPos.contains(pos)) {
                        added.add(pos);
                    }
                }
            }
        }

        // Process added chunks
        iterator = added.iterator();
        while (iterator.hasNext()) {
            long chunk = iterator.nextLong();
            HashSet<NextTickListEntry> entries = chunkTickMap.get(chunk);
            if (entries != null) {
                pendingTickCandidates.addAll(entries);
            }
        }
        simulationDistanceOld = simulationDistance;
    }

    public void tickStart() {
        World world = worldRef.get();
        if (world == null) {
            return;
        }

        mergeNoTickChunkChanges();

        // Processing cache is only valid for one tick
        cacheShouldProcessTick.clear();

        // Reset forced chunk cache
        LongOpenHashSet forcedChunksOld = new LongOpenHashSet(forcedChunksMap);
        forcedChunksMap.clear();
        for (ChunkCoordIntPair key : ForgeChunkManager.getPersistentChunksFor(world).keys()) {
            forcedChunksMap.add(ChunkCoordIntPair.chunkXZ2Int(key.chunkXPos, key.chunkZPos));
        }

        if (!isServer) {
            return;
        }

        checkForAddedChunks(forcedChunksOld);

        // Reset player positions
        playerPosOldMap.clear();
        for (EntityPlayer player : world.playerEntities) {
            playerPosOldMap.put(player, new ChunkCoordIntPair((int) player.posX >> 4, (int) player.posZ >> 4));
        }
    }

    private void dumpTickLists(NextTickListEntry currentEntry) {
        FMLLog.info("Trying to process entry: " + currentEntry);
        FMLLog.info("pendingTickListEntriesTreeSet:");
        for (NextTickListEntry entry : pendingTickListEntriesTreeSet) {
            FMLLog.info("    " + entry);
        }
        FMLLog.info("pendingTickListEntriesHashSet:");
        for (NextTickListEntry entry : pendingTickListEntriesHashSet) {
            FMLLog.info("    " + entry);
        }
    }

    public void chunkUnloaded(long chunk) {
        World world = worldRef.get();
        if (world == null) {
            return;
        }

        HashSet<NextTickListEntry> entries = chunkTickMap.get(chunk);
        if (!isServer || entries == null) {
            return;
        }

        chunkTickMap.remove(chunk);
        for (NextTickListEntry entry : entries) {
            if (!pendingTickListEntriesTreeSet.remove(entry)) {
                dumpTickLists(entry);
                throw new IllegalStateException("Failed to remove tick! See logs for more.");
            }
            if (!pendingTickListEntriesHashSet.remove(entry)) {
                dumpTickLists(entry);
                throw new IllegalStateException("Failed to remove tick! See logs for more.");
            }
            if (Compat.isCoreTweaksPresent()) {
                CoreTweaksCompat.removeTickEntry(world, entry);
            }
            /*
             * Entries would get removed in tickUpdates eventually, but we risk reloading a chunk and having an
             * incompatible, similar entry in pendingTickListEntriesTreeSet/pendingTickListEntriesHashSet that can be
             * removed only from the hashset due to a different equals/comparable check
             */

            pendingTickCandidates.remove(entry);
        }
    }

    private void removeTick(NextTickListEntry entry) {
        World world = worldRef.get();
        if (world == null) {
            return;
        }

        if (!pendingTickListEntriesTreeSet.remove(entry)) {
            dumpTickLists(entry);
            throw new IllegalStateException("Failed to remove tick! See logs for more.");
        }
        if (!pendingTickListEntriesHashSet.remove(entry)) {
            dumpTickLists(entry);
            throw new IllegalStateException("Failed to remove tick! See logs for more.");
        }
        if (Compat.isCoreTweaksPresent()) {
            CoreTweaksCompat.removeTickEntry(world, entry);
        }
        long key = ChunkCoordIntPair.chunkXZ2Int(entry.xCoord >> 4, entry.zCoord >> 4);
        HashSet<NextTickListEntry> entries = chunkTickMap.get(key);
        if (entries != null) {
            entries.remove(entry);
        }
    }

    public void addTick(NextTickListEntry entry) {
        pendingTickCandidates.add(entry);
        long key = ChunkCoordIntPair.chunkXZ2Int(entry.xCoord >> 4, entry.zCoord >> 4);
        HashSet<NextTickListEntry> entries = chunkTickMap.get(key);
        if (entries == null) {
            entries = new HashSet<>();
            chunkTickMap.put(key, entries);
        }
        entries.add(entry);

    }

    public void tickUpdates(boolean processAll, List<NextTickListEntry> pendingTickListEntriesThisTick) {
        World world = worldRef.get();
        if (world == null) {
            return;
        }

        if (pendingTickListEntriesTreeSet.size() != pendingTickListEntriesHashSet.size()) {
            throw new IllegalStateException("TickNextTick list out of sync");
        }

        Iterator<NextTickListEntry> iterator = pendingTickCandidates.iterator();
        while (iterator.hasNext()) {
            NextTickListEntry entry = iterator.next();
            if (!processAll && entry.scheduledTime > world.getWorldInfo().getWorldTotalTime()) {
                break;
            }

            iterator.remove(); // Remove ignored entries as well
            if (!chunkExists.test(entry.xCoord >> 4, entry.zCoord >> 4)) {
                removeTick(entry);
            } else if (shouldProcessTick(entry.xCoord >> 4, entry.zCoord >> 4)) {
                removeTick(entry);
                pendingTickListEntriesThisTick.add(entry);
                if (pendingTickListEntriesThisTick.size() >= 1000) {
                    break;
                }
            }
        }
    }

    public void setServerVariables(TreeSet<NextTickListEntry> pendingTickListEntriesTreeSet,
            Set<NextTickListEntry> pendingTickListEntriesHashSet, BiPredicate<Integer, Integer> chunkExists) {
        this.pendingTickListEntriesTreeSet = pendingTickListEntriesTreeSet;
        this.pendingTickListEntriesHashSet = pendingTickListEntriesHashSet;
        this.chunkExists = chunkExists;
    }

    static class LongBooleanArrayList {

        private final LongArrayList longs = new LongArrayList();
        private final BooleanArrayList booleans = new BooleanArrayList();

        public void add(long packedChunkPos, boolean prevent) {
            longs.add(packedChunkPos);
            booleans.add(prevent);
        }

        public int size() {
            return longs.size();
        }

        public long getLong(int i) {
            return longs.getLong(i);
        }

        public boolean getBoolean(int i) {
            return booleans.getBoolean(i);
        }

        public void clear() {
            longs.clear();
            booleans.clear();
        }
    }
}
