package com.mitchej123.hodgepodge.util;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import net.minecraft.server.MinecraftServer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.gtnewhorizon.gtnhlib.util.ServerThreadUtil;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;

/**
 * LongHashMap for server-side use (ChunkProviderServer). Writes are owner-thread-only. Off-thread reads use a
 * demand-driven snapshot of the map.
 * <p>
 * Snapshot lifecycle:
 * <ul>
 * <li>First {@code add()} eagerly creates the initial snapshot (already on the server thread).</li>
 * <li>Off-thread reads return the existing snapshot immediately in the common case.</li>
 * <li>If no snapshot exists yet, off-thread reads fall back to {@code callFromMainThread} with a 250ms timeout,
 * returning a stale snapshot if available or an empty map on timeout.</li>
 * <li>{@link #refreshSnapshots()} (called from server tick END) refreshes snapshots every
 * {@link #SNAPSHOT_INTERVAL_TICKS} ticks while off-thread reads are active.</li>
 * <li>If no off-thread read occurs for {@link #IDLE_TIMEOUT_TICKS}, refreshing stops but the snapshot is retained as a
 * stale fallback.</li>
 * </ul>
 */
@SuppressWarnings("unused")
public class ServerThreadLongHashMap extends FastUtilLongHashMap {

    private static final Logger LOGGER = LogManager.getLogger("ServerThreadLongHashMap");
    private static final Set<String> loggedThreadNames = Collections.newSetFromMap(new ConcurrentHashMap<>());
    private static final List<WeakReference<ServerThreadLongHashMap>> snapshottingInstances = new ArrayList<>();

    /** How often to refresh the snapshot (in server ticks). ~1 second. */
    private static final int SNAPSHOT_INTERVAL_TICKS = 20;

    /** Stop snapshotting if no off-thread read in this many ticks. ~10 seconds. */
    private static final int IDLE_TIMEOUT_TICKS = 200;

    private int snapshotTick;
    private volatile int lastAccessTick;
    private volatile boolean registeredForTicking;
    private boolean dirty;
    private volatile Long2ObjectOpenHashMap<Object> snapshot;

    public static void refreshSnapshots() {
        synchronized (snapshottingInstances) {
            snapshottingInstances.removeIf(ref -> {
                final ServerThreadLongHashMap instance = ref.get();
                if (instance == null) return true;
                instance.refreshSnapshot();
                return !instance.registeredForTicking;
            });
        }
    }

    private void registerForTicking() {
        synchronized (snapshottingInstances) {
            if (registeredForTicking) return;
            registeredForTicking = true;
            snapshottingInstances.add(new WeakReference<>(this));
        }
    }

    private int currentTick() {
        final MinecraftServer server = MinecraftServer.getServer();
        return server != null ? server.getTickCounter() : 0;
    }

    public static void clearSnapshots() {
        synchronized (snapshottingInstances) {
            snapshottingInstances.clear();
        }
        loggedThreadNames.clear();
    }

    private void refreshSnapshot() {
        if (Thread.currentThread() != ownerThread) {
            throw new IllegalStateException(
                    "tickSnapshot called from " + Thread.currentThread().getName()
                            + ", expected "
                            + ownerThread.getName());
        }
        if (snapshot == null) {
            return;
        }
        final int tick = currentTick();
        if ((tick - lastAccessTick) > IDLE_TIMEOUT_TICKS) {
            if (dirty) {
                // One final snapshot to release removed entries before going idle
                doSnapshot();
            }
            registeredForTicking = false;
            return;
        }
        if ((tick - snapshotTick) >= SNAPSHOT_INTERVAL_TICKS && dirty) {
            doSnapshot();
        }
    }

    private void doSnapshot() {
        snapshot = new Long2ObjectOpenHashMap<>(map);
        snapshotTick = currentTick();
        dirty = false;
    }

    @Override
    public void add(long key, Object value) {
        super.add(key, value);
        if (snapshot == null) {
            // Proactively make an initial snapshot, so we always have one
            registerForTicking();
            doSnapshot();
        } else {
            dirty = true;
        }
    }

    @Override
    public Object remove(long key) {
        final Object removed = super.remove(key);
        if (removed != null && snapshot != null) {
            dirty = true;
            registerForTicking();
        }
        return removed;
    }

    private Long2ObjectOpenHashMap<Object> getSnapshot() {
        lastAccessTick = currentTick();

        if (registeredForTicking) {
            final Long2ObjectOpenHashMap<Object> snap = snapshot;
            if (snap != null) {
                return snap;
            }
        }

        synchronized (this) {
            registerForTicking();
            try {
                ServerThreadUtil.callFromMainThread(() -> {
                    doSnapshot();
                    return null;
                }).get(250, TimeUnit.MILLISECONDS);
            } catch (TimeoutException | InterruptedException e) {
                if (e instanceof InterruptedException) Thread.currentThread().interrupt();
                final Long2ObjectOpenHashMap<Object> fallback = snapshot != null ? snapshot
                        : new Long2ObjectOpenHashMap<>();
                LOGGER.warn(
                        "Snapshot creation timed out (server thread may be blocked) - serving {} map to {}",
                        snapshot != null ? "stale" : "empty",
                        Thread.currentThread().getName());
                return fallback;
            } catch (ExecutionException | IllegalStateException e) {
                throw new RuntimeException("Failed to create chunk map snapshot", e);
            }
            return snapshot;
        }
    }

    private void logOffThread() {
        if (loggedThreadNames.add(Thread.currentThread().getName())) {
            LOGGER.warn(
                    "Off-thread read from {} - serving from snapshot",
                    Thread.currentThread().getName(),
                    new Throwable("Caller stacktrace"));
        }
    }

    @Override
    public Object getValueByKey(long key) {
        if (Thread.currentThread() == ownerThread) {
            return map.get(key);
        }
        logOffThread();
        return getSnapshot().get(key);
    }

    @Override
    public boolean containsItem(long key) {
        if (Thread.currentThread() == ownerThread) {
            return map.containsKey(key);
        }
        logOffThread();
        return getSnapshot().containsKey(key);
    }

    @Override
    public int getNumHashElements() {
        if (Thread.currentThread() == ownerThread) {
            return map.size();
        }
        logOffThread();
        return getSnapshot().size();
    }

    @Override
    public Iterator<Object> valuesIterator() {
        if (Thread.currentThread() == ownerThread) {
            return map.values().iterator();
        }
        logOffThread();
        return new ArrayList<>(getSnapshot().values()).iterator();
    }
}
