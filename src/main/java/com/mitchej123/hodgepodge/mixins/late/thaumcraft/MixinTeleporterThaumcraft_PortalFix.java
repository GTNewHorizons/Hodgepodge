package com.mitchej123.hodgepodge.mixins.late.thaumcraft;

import net.minecraft.entity.Entity;
import net.minecraft.world.WorldServer;
import net.minecraft.world.gen.ChunkProviderServer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mitchej123.hodgepodge.mixins.hooks.ChunkGenScheduler;

import thaumcraft.common.lib.world.dim.TeleporterThaumcraft;

/**
 * Fixes players spawning at abnormally high Y positions when first entering a new Thaumcraft eldritch portal (before
 * the portal structure has been generated in that chunk).
 *
 * <p>
 * Root cause (multiplayer / throttleChunkGeneration): {@code TeleporterThaumcraft.placeInPortal} scans for the
 * eldritch portal block via {@code world.getBlock()}, which goes through {@code provideChunk()}. Hodgepodge's
 * EntityGuard mixin returns {@code EmptyChunk} for unloaded chunks while chunk loading is blocked (during most of the
 * server tick when {@code throttleChunkGeneration} is active). This causes the scan to see only air everywhere, find no
 * portal block, and return without repositioning the player — leaving them at their overworld Y coordinate (e.g. Y=64),
 * which is above the maze structure placed at Y=50–63.
 *
 * <p>
 * Root cause (singleplayer / preventChunkLoadingFromBlockUpdates): {@code MixinWorldServer_PreventChunkLoading} sets
 * {@code ChunkProviderServer.loadChunkOnProvideRequest = false} for the duration of entity/tile-entity ticking. The
 * eldritch portal tile entity runs during this window, so when {@code placeInExistingPortal} calls
 * {@code provideChunk()} for unloaded chunks, even vanilla's {@code provideChunk} returns {@code defaultEmptyChunk}
 * (because neither {@code findingSpawnPoint} nor {@code loadChunkOnProvideRequest} is true). The scan finds no portal
 * block and leaves the player at their overworld Y.
 *
 * <p>
 * Fix: at the start of {@code placeInPortal}, temporarily restore both chunk-loading gates — unblock the
 * {@link ChunkGenScheduler} flag and re-enable {@code loadChunkOnProvideRequest} on the destination world's
 * {@link ChunkProviderServer}. Both are restored to their original values when {@code placeInPortal} returns.
 */
@Mixin(value = TeleporterThaumcraft.class, remap = false)
public class MixinTeleporterThaumcraft_PortalFix {

    @Shadow(remap = false)
    private WorldServer worldServerInstance;

    @Unique
    private boolean hodgepodge$wasChunkLoadBlocked;

    @Unique
    private boolean hodgepodge$wasLoadChunkOnProvideRequestDisabled;

    @Inject(method = "placeInPortal", at = @At("HEAD"))
    private void hodgepodge$unblockForPortalSearch(Entity entity, double x, double y, double z, float yaw,
            CallbackInfo ci) {
        hodgepodge$wasChunkLoadBlocked = ChunkGenScheduler.isBlocked();
        if (hodgepodge$wasChunkLoadBlocked) {
            ChunkGenScheduler.enableChunkLoads();
        }

        // Also re-enable direct chunk loading via provideChunk().
        // MixinWorldServer_PreventChunkLoading sets loadChunkOnProvideRequest=false for the entire entity-tick window,
        // which includes the tile-entity update that triggers this teleportation.  Without this, vanilla provideChunk
        // returns defaultEmptyChunk even when ChunkGenScheduler is not blocking, causing the portal scan to fail.
        final ChunkProviderServer cps = worldServerInstance.theChunkProviderServer;
        hodgepodge$wasLoadChunkOnProvideRequestDisabled = !cps.loadChunkOnProvideRequest;
        if (hodgepodge$wasLoadChunkOnProvideRequestDisabled) {
            cps.loadChunkOnProvideRequest = true;
        }
    }

    @Inject(method = "placeInPortal", at = @At("RETURN"))
    private void hodgepodge$reblockAfterPortalSearch(Entity entity, double x, double y, double z, float yaw,
            CallbackInfo ci) {
        if (hodgepodge$wasChunkLoadBlocked) {
            ChunkGenScheduler.disableChunkLoads();
        }
        if (hodgepodge$wasLoadChunkOnProvideRequestDisabled) {
            worldServerInstance.theChunkProviderServer.loadChunkOnProvideRequest = false;
        }
    }
}
