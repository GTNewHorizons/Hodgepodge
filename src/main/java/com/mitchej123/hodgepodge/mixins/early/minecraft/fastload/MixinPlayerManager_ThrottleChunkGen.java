package com.mitchej123.hodgepodge.mixins.early.minecraft.fastload;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.management.PlayerManager;
import net.minecraft.util.MathHelper;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.AnvilChunkLoader;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraftforge.common.util.ChunkCoordComparator;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mitchej123.hodgepodge.mixins.hooks.ChunkGenScheduler;
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

    /**
     * @author mitchej123
     * @reason Throttle chunk generation to reduce server stalls when players traverse through ungenerated terrain.
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

                player.loadedChunks.removeIf(
                        c -> Math.abs(c.chunkXPos - newCX) > viewRadius || Math.abs(c.chunkZPos - newCZ) > viewRadius);

                player.managedPosX = player.posX;
                player.managedPosZ = player.posZ;

                chunksToLoad.sort((a, b) -> {
                    final int ax = ChunkPosUtil.getPackedX(a) - newCX;
                    final int az = ChunkPosUtil.getPackedZ(a) - newCZ;
                    final int bx = ChunkPosUtil.getPackedX(b) - newCX;
                    final int bz = ChunkPosUtil.getPackedZ(b) - newCZ;
                    return Integer.compare(ax * ax + az * az, bx * bx + bz * bz);
                });

                // Classify each chunk: load immediately if available, defer if needing worldgen
                final ChunkProviderServer cps = this.theWorldServer.theChunkProviderServer;
                final AnvilChunkLoader acl = cps.currentChunkLoader instanceof AnvilChunkLoader
                        ? (AnvilChunkLoader) cps.currentChunkLoader
                        : null;

                final LongArrayList needsGen = new LongArrayList();
                final ChunkGenScheduler scheduler = ChunkGenScheduler
                        .forDimension(this.theWorldServer.provider.dimensionId);

                for (int i = 0; i < chunksToLoad.size(); i++) {
                    final long packed = chunksToLoad.getLong(i);
                    final int x = ChunkPosUtil.getPackedX(packed);
                    final int z = ChunkPosUtil.getPackedZ(packed);
                    if (cps.chunkExists(x, z) || (acl != null && acl.chunkExists(this.theWorldServer, x, z))) {
                        this.getOrCreateChunkWatcher(x, z, true).addPlayer(player);
                        // Re-track orphaned chunks: loaded in memory with terrain but never populated
                        final Chunk chunk = cps.provideChunk(x, z);
                        if (chunk != null && !chunk.isTerrainPopulated) {
                            scheduler.trackIfUnpopulated(chunk, x, z);
                        }
                    } else {
                        needsGen.add(packed);
                    }
                }

                if (!needsGen.isEmpty()) {
                    ChunkGenScheduler.forDimension(this.theWorldServer.provider.dimensionId)
                            .enqueueForPlayer(player, needsGen);
                }

                if (viewRadius > 1 || Math.abs(deltaX) > 1 || Math.abs(deltaZ) > 1) {
                    player.loadedChunks.sort(new ChunkCoordComparator(player));
                }
            }
        }
    }

    @Inject(method = "updatePlayerInstances", at = @At("TAIL"))
    private void hodgepodge$processDeferredChunks(CallbackInfo ci) {
        final int dimId = this.theWorldServer.provider.dimensionId;
        final ChunkProviderServer cps = this.theWorldServer.theChunkProviderServer;
        final AnvilChunkLoader acl = cps.currentChunkLoader instanceof AnvilChunkLoader
                ? (AnvilChunkLoader) cps.currentChunkLoader
                : null;

        ChunkGenScheduler.forDimension(dimId)
                .processTick(this.theWorldServer, cps, this.playerViewRadius, acl, (cx, cz, players) -> {
                    final var pi = this.getOrCreateChunkWatcher(cx, cz, true);
                    for (var p : players) pi.addPlayer(p);
                });
    }

    @Inject(method = "removePlayer", at = @At("HEAD"))
    private void hodgepodge$cleanupDeferredChunks(EntityPlayerMP player, CallbackInfo ci) {
        ChunkGenScheduler.forDimension(this.theWorldServer.provider.dimensionId).removePlayer(player);
    }
}
