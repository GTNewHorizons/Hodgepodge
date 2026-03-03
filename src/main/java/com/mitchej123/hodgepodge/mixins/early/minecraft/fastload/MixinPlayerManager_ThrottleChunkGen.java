package com.mitchej123.hodgepodge.mixins.early.minecraft.fastload;

import java.util.IdentityHashMap;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerManager;
import net.minecraft.util.MathHelper;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.storage.AnvilChunkLoader;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraftforge.common.util.ChunkCoordComparator;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mitchej123.hodgepodge.config.SpeedupsConfig;
import com.mitchej123.hodgepodge.mixins.hooks.DeferredChunkQueue;
import com.mitchej123.hodgepodge.util.ChunkPosUtil;

import it.unimi.dsi.fastutil.longs.LongArrayList;

@Mixin(PlayerManager.class)
public abstract class MixinPlayerManager_ThrottleChunkGen {

    @Shadow
    @Final
    private WorldServer theWorldServer;

    @Shadow
    private int playerViewRadius;

    @Shadow
    private PlayerManager.PlayerInstance getOrCreateChunkWatcher(int x, int z, boolean create) {
        return null;
    }

    @Shadow
    private boolean overlaps(int x1, int z1, int x2, int z2, int radius) {
        return false;
    }

    @Unique
    private final Map<EntityPlayerMP, DeferredChunkQueue> hodgepodge$deferredChunks = new IdentityHashMap<>();

    /** Accumulated nanoseconds of chunk-gen overspend, paid down by the per-tick budget. */
    @Unique
    private long hodgepodge$genTimeDebtNanos = 0;

    @Unique
    private boolean hodgepodge$amortizeOverruns;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void hodgepodge$resolveAmortize(CallbackInfo ci) {
        hodgepodge$amortizeOverruns = switch (SpeedupsConfig.amortizeChunkGenOverruns) {
            case Always -> true;
            case Never -> false;
            case DedicatedServerOnly -> MinecraftServer.getServer().isDedicatedServer();
        };
    }

    /**
     * @author mitchej123
     * @reason Throttle chunk generation to reduces server stalls when players traverse through ungenerated terrain.
     */
    @Overwrite
    public void updatePlayerPertinentChunks(EntityPlayerMP player) {
        final int newCX = MathHelper.floor_double(player.posX) >> 4;
        final int newCZ = MathHelper.floor_double(player.posZ) >> 4;
        final double dx = player.managedPosX - player.posX;
        final double dz = player.managedPosZ - player.posZ;
        final double distSq = dx * dx + dz * dz;

        if (distSq >= 64.0D) {
            final int oldCX = MathHelper.floor_double(player.managedPosX) >> 4;
            final int oldCZ = MathHelper.floor_double(player.managedPosZ) >> 4;
            final int viewRadius = this.playerViewRadius;
            final int deltaX = newCX - oldCX;
            final int deltaZ = newCZ - oldCZ;
            final int side = 2 * viewRadius + 1;
            final LongArrayList chunksToLoad = new LongArrayList(side * 2);

            if (deltaX != 0 || deltaZ != 0) {
                // Build list of new chunks and remove player from old chunks — same as vanilla,
                // plus recover chunks in the overlap that were deferred but never loaded
                for (int x = newCX - viewRadius; x <= newCX + viewRadius; ++x) {
                    for (int z = newCZ - viewRadius; z <= newCZ + viewRadius; ++z) {
                        if (!this.overlaps(x, z, oldCX, oldCZ, viewRadius)) {
                            chunksToLoad.add(ChunkPosUtil.toLong(x, z));
                        } else if (this.getOrCreateChunkWatcher(x, z, false) == null) {
                            // In overlap but no PlayerInstance — was deferred and pruned
                            chunksToLoad.add(ChunkPosUtil.toLong(x, z));
                        }

                        if (!this.overlaps(x - deltaX, z - deltaZ, newCX, newCZ, viewRadius)) {
                            final PlayerManager.PlayerInstance pi = this
                                    .getOrCreateChunkWatcher(x - deltaX, z - deltaZ, false);
                            if (pi != null) {
                                pi.removePlayer(player);
                            }
                        }
                    }
                }

                // Replace filterChunkLoadQueue: remove out-of-range entries without the side effect of creating
                // PlayerInstances (which triggers synchronous loadChunk)
                player.loadedChunks.removeIf(
                        c -> Math.abs(c.chunkXPos - newCX) > viewRadius || Math.abs(c.chunkZPos - newCZ) > viewRadius);

                player.managedPosX = player.posX;
                player.managedPosZ = player.posZ;

                // Send nearest chunks first — same as vanilla
                final int sortCX = newCX;
                final int sortCZ = newCZ;
                chunksToLoad.sort((a, b) -> {
                    final int ax = ChunkPosUtil.getPackedX(a) - sortCX;
                    final int az = ChunkPosUtil.getPackedZ(a) - sortCZ;
                    final int bx = ChunkPosUtil.getPackedX(b) - sortCX;
                    final int bz = ChunkPosUtil.getPackedZ(b) - sortCZ;
                    return Integer.compare(ax * ax + az * az, bx * bx + bz * bz);
                });

                // Classify each chunk: load immediately if available, defer if needing worldgen
                final ChunkProviderServer cps = this.theWorldServer.theChunkProviderServer;
                final AnvilChunkLoader acl = cps.currentChunkLoader instanceof AnvilChunkLoader
                        ? (AnvilChunkLoader) cps.currentChunkLoader
                        : null;

                DeferredChunkQueue dq = null;

                for (int i = 0; i < chunksToLoad.size(); i++) {
                    final long packed = chunksToLoad.getLong(i);
                    final int x = ChunkPosUtil.getPackedX(packed);
                    final int z = ChunkPosUtil.getPackedZ(packed);
                    if (cps.chunkExists(x, z) || (acl != null && acl.chunkExists(this.theWorldServer, x, z))) {
                        // In memory or on disk — load immediately
                        this.getOrCreateChunkWatcher(x, z, true).addPlayer(player);
                    } else {
                        // Needs worldgen — defer to per-tick processing
                        if (dq == null) {
                            dq = hodgepodge$deferredChunks.computeIfAbsent(player, k -> new DeferredChunkQueue());
                        }
                        if (!dq.chunks.contains(packed)) {
                            dq.chunks.add(packed);
                            dq.dirty = true;
                        }
                    }
                }

                // Re-sort loadedChunks if player moved
                if (viewRadius > 1 || Math.abs(deltaX) > 1 || Math.abs(deltaZ) > 1) {
                    player.loadedChunks.sort(new ChunkCoordComparator(player));
                }
            }
        }
    }

    /**
     * Process deferred chunk generation queue each tick, limited by count and optional time budget.
     *
     * Since getOrCreateChunkWatcher is synchronous, a single chunk could exceed the budgets. When
     * {@code amortizeChunkGenOverruns} is enabled, a time-debt mechanism records the overspend and pays it down at
     * {@code maxTimeNanos} per tick, skipping generation (but not pruning) until the debt clears. This keeps average
     * gen time per tick at the configured budget.
     */
    @Inject(method = "updatePlayerInstances", at = @At("TAIL"))
    private void hodgepodge$processDeferredChunks(CallbackInfo ci) {
        if (hodgepodge$deferredChunks.isEmpty()) return;

        final int timeBudgetMs = SpeedupsConfig.maxChunkGenTimePerTick;
        final long maxTimeNanos = timeBudgetMs * 1_000_000L;

        // Pay down debt from previous overruns.
        final boolean inDebt;
        if (hodgepodge$amortizeOverruns && maxTimeNanos > 0 && hodgepodge$genTimeDebtNanos > 0) {
            hodgepodge$genTimeDebtNanos -= maxTimeNanos;
            inDebt = true;
        } else {
            hodgepodge$genTimeDebtNanos = 0;
            inDebt = false;
        }

        final long globalStartTime = !inDebt && maxTimeNanos > 0 ? System.nanoTime() : 0;
        final long globalDeadline = globalStartTime + maxTimeNanos;
        final int activePlayerCount = hodgepodge$deferredChunks.size();
        final long perPlayerTimeNanos = maxTimeNanos > 0 ? maxTimeNanos / activePlayerCount : 0;
        final int viewRadius = this.playerViewRadius;
        final ChunkProviderServer cps = this.theWorldServer.theChunkProviderServer;
        boolean globalBudgetExceeded = false;

        final var iterator = hodgepodge$deferredChunks.entrySet().iterator();
        while (iterator.hasNext()) {
            final var entry = iterator.next();
            final EntityPlayerMP player = entry.getKey();
            final DeferredChunkQueue dq = entry.getValue();
            final LongArrayList queue = dq.chunks;

            if (queue.isEmpty()) {
                iterator.remove();
                continue;
            }

            final int cx = MathHelper.floor_double(player.posX) >> 4;
            final int cz = MathHelper.floor_double(player.posZ) >> 4;

            // Prune out-of-range chunks and load any that have since arrived in memory
            queue.removeIf(packed -> {
                final int x = ChunkPosUtil.getPackedX(packed);
                final int z = ChunkPosUtil.getPackedZ(packed);
                if (Math.abs(x - cx) > viewRadius || Math.abs(z - cz) > viewRadius) return true;
                if (cps.chunkExists(x, z)) {
                    this.getOrCreateChunkWatcher(x, z, true).addPlayer(player);
                    return true;
                }
                return false;
            });

            if (queue.isEmpty()) {
                iterator.remove();
                continue;
            }

            // Skip generation while paying off debt or after global budget exceeded
            if (inDebt || globalBudgetExceeded) continue;

            // Sort descending: nearest/forward-biased chunks last so tail-processing picks them first
            final boolean playerMoved = player.posX != player.prevPosX || player.posZ != player.prevPosZ;
            if (playerMoved || dq.dirty) {
                final double motionX = player.posX - player.prevPosX;
                final double motionZ = player.posZ - player.prevPosZ;
                queue.sort((a, b) -> {
                    final int adx = ChunkPosUtil.getPackedX(a) - cx;
                    final int adz = ChunkPosUtil.getPackedZ(a) - cz;
                    final int bdx = ChunkPosUtil.getPackedX(b) - cx;
                    final int bdz = ChunkPosUtil.getPackedZ(b) - cz;
                    final double aScore = adx * adx + adz * adz - (adx * motionX + adz * motionZ) * 2.0;
                    final double bScore = bdx * bdx + bdz * bdz - (bdx * motionX + bdz * motionZ) * 2.0;
                    return Double.compare(bScore, aScore);
                });
                dq.dirty = false;
            }

            // Process from tail (nearest chunks)
            final int size = queue.size();
            final int limit = Math.min(size, SpeedupsConfig.maxChunkGenPerPlayerPerTick);

            long deadline;
            if (maxTimeNanos > 0) {
                final long now = System.nanoTime();
                final long playerDeadline = now + perPlayerTimeNanos;
                deadline = Math.min(playerDeadline, globalDeadline);
            } else {
                deadline = Long.MAX_VALUE;
            }

            int processed = 0;
            for (int i = size - 1; i >= size - limit && i >= 0; i--) {
                if (maxTimeNanos > 0 && System.nanoTime() > deadline) {
                    break;
                }
                final long packed = queue.getLong(i);
                this.getOrCreateChunkWatcher(ChunkPosUtil.getPackedX(packed), ChunkPosUtil.getPackedZ(packed), true)
                        .addPlayer(player);
                processed++;
            }
            if (processed > 0) {
                queue.removeElements(size - processed, size);
            }
            if (maxTimeNanos > 0 && System.nanoTime() > globalDeadline) {
                globalBudgetExceeded = true;
            }
        }

        // Record overrun as debt to be paid down on future ticks
        if (hodgepodge$amortizeOverruns && !inDebt && maxTimeNanos > 0 && globalStartTime > 0) {
            final long elapsed = System.nanoTime() - globalStartTime;
            if (elapsed > maxTimeNanos) {
                // Cap debt at 40x budget (~2s at default 15ms) to bound maximum generation pause
                hodgepodge$genTimeDebtNanos = Math.min(elapsed - maxTimeNanos, maxTimeNanos * 40);
            }
        }
    }

    @Inject(method = "removePlayer", at = @At("HEAD"))
    private void hodgepodge$cleanupDeferredChunks(EntityPlayerMP player, CallbackInfo ci) {
        hodgepodge$deferredChunks.remove(player);
    }
}
