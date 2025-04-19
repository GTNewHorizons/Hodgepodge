package com.mitchej123.hodgepodge;

import com.mitchej123.hodgepodge.config.FixesConfig;
import com.mitchej123.hodgepodge.config.TweaksConfig;
import it.unimi.dsi.fastutil.longs.Long2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ByteOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongBooleanPair;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.NextTickListEntry;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.ForgeChunkManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.BiPredicate;

public class SimulationDistanceHelper {
    /**
     * Mark a chunk as no to be simulated, or reset that state.
     */
    public static void preventChunkSimulation(World world, ChunkCoordIntPair chunk, boolean prevent) {
        if (!FixesConfig.addSimulationDistance) {
            return;
        }
        ISimulationDistanceWorld mixin = (ISimulationDistanceWorld) world;
        mixin.hodgepodge$preventChunkSimulation(chunk, prevent);
    }

    public static int getSimulationDistance() {
        return TweaksConfig.simulationDistance;
    }

    public static void setSimulationDistance(int distance) {
        TweaksConfig.simulationDistance = distance;
    }

    public SimulationDistanceHelper(World world) {
        this.world = world;
        isServer = world instanceof WorldServer;
    }

    /**
     * The current world
     */
    private final World world;

    /**
     * True if we have a WorldServer
     */
    private final boolean isServer;

    /**
     * Chunks that should not be ticked
     */
    private final Long2ByteOpenHashMap noTickChunks = new Long2ByteOpenHashMap();

    /**
     * Changes to the chunks that should not be ticked. Needed since we only update the real map once a tick.
     * Reason is that forced chunks are also cached and updated once a tick,  we want both in sync.
     */
    private List<LongBooleanPair> noTickChunksChanges = Collections.synchronizedList(new ArrayList<>());

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
    private final Map<EntityPlayer, ChunkCoordIntPair> playerPosOldMap = new HashMap<>();

    /**
     * All ticks that are currently considered.
     */
    private final TreeSet<NextTickListEntry> pendingTickCandidates = new TreeSet<>();

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

    public void preventChunkSimulation(ChunkCoordIntPair chunk, boolean prevent) {
        long key = ChunkCoordIntPair.chunkXZ2Int(chunk.chunkXPos, chunk.chunkZPos);
        noTickChunksChanges.add(LongBooleanPair.of(key, prevent));
    }

    private boolean closeToPlayer(int x, int z) {
        int simulationDistance = SimulationDistanceHelper.getSimulationDistance();
        for (EntityPlayer player : world.playerEntities) {
            if (player.getEntityWorld() != world) {
                continue;
            }
            int playerX = (int) player.posX >> 4;
            int playerZ = (int) player.posZ >> 4;
            if (Math.abs(playerX - x) <= simulationDistance && Math.abs(playerZ - z) <= simulationDistance) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if a chunk should get processed
     */
    public boolean shouldProcessTick(int x, int z) {
        long key = ChunkCoordIntPair.chunkXZ2Int(x, z);
        return cacheShouldProcessTick.computeIfAbsent(key, dummy -> {
            if (closeToPlayer(x, z)) {
                return true;
            }
            if (noTickChunks.containsKey(key)) {
                return false;
            }
            return forcedChunksMap.contains(key);
        });
    }

    private LongOpenHashSet getPlayerChunksForPos(ChunkCoordIntPair pos) {
        int simulationDistance = SimulationDistanceHelper.getSimulationDistance();
        LongOpenHashSet chunks = new LongOpenHashSet();
        for (int x = -simulationDistance; x <= simulationDistance; x++) {
            for (int z = -simulationDistance; z <= simulationDistance; z++) {
                chunks.add(ChunkCoordIntPair.chunkXZ2Int(pos.chunkXPos + x, pos.chunkZPos + z));
            }
        }
        return chunks;
    }

    private void mergeNoTickChunkChanges() {
        List<LongBooleanPair> changes = noTickChunksChanges;
        noTickChunksChanges = Collections.synchronizedList(new ArrayList<>());
        for (LongBooleanPair update : changes) {
            long key = update.keyLong();
            boolean prevent = update.valueBoolean();
            byte value = noTickChunks.getOrDefault(key, (byte) 0);
            value += (byte) (prevent ? 1 : -1);
            if (value > 0) {
                noTickChunks.put(key, value);
            } else {
                noTickChunks.remove(key);
            }
        }
    }

    private void checkForAddedChunks(LongOpenHashSet forcedChunksOld) {
        LongOpenHashSet added = new LongOpenHashSet();

        // New forced chunks?
        for (long chunk : forcedChunksMap) {
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
            if (playerPosOld == null) {
                LongOpenHashSet chunksNewPos = getPlayerChunksForPos(playerPos);
                added.addAll(chunksNewPos);
            } else if (!playerPos.equals(playerPosOld)) {
                LongOpenHashSet chunksNewPos = getPlayerChunksForPos(playerPos);
                LongOpenHashSet chunksOldPos = getPlayerChunksForPos(playerPosOld);
                for (long pos : chunksNewPos) {
                    if (!chunksOldPos.contains(pos)) {
                        added.add(pos);
                    }
                }
            }
        }

        // Process added chunks
        for (long chunk : added) {
            HashSet<NextTickListEntry> entries = chunkTickMap.get(chunk);
            if (entries != null) {
                pendingTickCandidates.addAll(entries);
            }
        }
    }

    public void tickStart() {
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

    public void chunkUnloaded(long chunk) {
        HashSet<NextTickListEntry> entries = chunkTickMap.get(chunk);
        if (!isServer || entries == null) {
            return;
        }

        chunkTickMap.remove(chunk);
        for (NextTickListEntry entry : entries) {
            pendingTickListEntriesTreeSet.remove(entry);
            pendingTickListEntriesHashSet.remove(entry);
            if (Compat.isCoreTweaksPresent()) {
                CoreTweaksCompat.removeTickEntry(world, entry);
            }
        }
    }

    private void removeTick(NextTickListEntry entry) {
        pendingTickListEntriesTreeSet.remove(entry);
        pendingTickListEntriesHashSet.remove(entry);
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
        if (pendingTickListEntriesTreeSet.size() != pendingTickListEntriesHashSet.size()) {
            throw new IllegalStateException("TickNextTick list out of synch");
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

    public void setServerVariables(TreeSet<NextTickListEntry> pendingTickListEntriesTreeSet, Set<NextTickListEntry> pendingTickListEntriesHashSet, BiPredicate<Integer, Integer> chunkExists) {
        this.pendingTickListEntriesTreeSet = pendingTickListEntriesTreeSet;
        this.pendingTickListEntriesHashSet = pendingTickListEntriesHashSet;
        this.chunkExists = chunkExists;
    }
}
