package com.mitchej123.hodgepodge.mixins.late.twilightforest;

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

import twilightforest.TFTeleporter;

/**
 * Fixes players spawning inside blocks (e.g. stuck in terrain) when entering the Twilight Forest portal for the first
 * time in a freshly-generated region.
 *
 * <h3>Root cause</h3>
 *
 * <p>
 * When a player steps through a Twilight Forest portal, Forge calls
 * {@code ServerConfigurationManager.transferEntityToWorld(..., TFTeleporter)}, which calls
 * {@code TFTeleporter.placeInPortal}. The teleporter first tries {@code placeInExistingPortal}: it spirals outward
 * scanning columns for {@code TFBlocks.portal} via {@code world.getBlock()}, which goes through
 * {@code ChunkProviderServer.provideChunk()}. Hodgepodge's EntityGuard mixin returns {@code EmptyChunk} for unloaded
 * chunks while chunk loading is throttled. With all chunks appearing empty, the scan finds no portal block and falls
 * through.
 *
 * <p>
 * {@code makePortal} is then called: it scans for suitable terrain via {@code world.isAirBlock()} /
 * {@code world.getBlock()}, which again hit only empty chunks, so it cannot find a proper spot and falls back to a
 * default position. {@code makePortalAt} issues many {@code world.setBlock()} calls to build the grass platform and
 * portal blocks — these are silently discarded when the target chunk is {@code EmptyChunk}. The follow-up
 * {@code placeInExistingPortal} call finds no portal (nothing was written), leaving the player at their raw translated
 * coordinates inside solid terrain.
 *
 * <h3>Fix</h3>
 *
 * <p>
 * The constructor injection permanently adds the Twilight Forest dimension ID to
 * {@link ChunkGenScheduler#excludeDimFromChunkThrottle(int)}, which bypasses EntityGuard,
 * {@code loadChunkOnProvideRequest} blocking, and population deferral for this dimension — ensuring that all chunk
 * accesses during portal placement and TF structure generation operate on real terrain.
 *
 * <p>
 * The {@code placeInPortal} HEAD/RETURN injections additionally unblock the global
 * {@link ChunkGenScheduler} flag and restore {@code loadChunkOnProvideRequest}, and proactively pre-load the entity's
 * destination chunk before the scan begins.
 */
@Mixin(value = TFTeleporter.class, remap = false)
public class MixinTFTeleporter_PortalFix {

    @Shadow(remap = false)
    protected WorldServer myWorld;

    @Unique
    private boolean hodgepodge$wasChunkLoadBlocked;

    @Unique
    private boolean hodgepodge$wasLoadChunkOnProvideRequestDisabled;

    /**
     * Register the Twilight Forest dimension as excluded from chunk-load throttling. This fires once per teleporter
     * instance (i.e. when the TF world is first accessed), permanently adding the dimension ID to the exclusion set
     * so that all subsequent chunk operations in that dimension bypass the throttle guards.
     */
    @Inject(method = "<init>", at = @At("RETURN"))
    private void hodgepodge$registerTwilightForestDim(WorldServer worldServer, CallbackInfo ci) {
        ChunkGenScheduler.excludeDimFromChunkThrottle(worldServer.provider.dimensionId);
    }

    @Inject(method = "placeInPortal", at = @At("HEAD"))
    private void hodgepodge$unblockForPortalSearch(Entity entity, double x, double y, double z, float facing,
            CallbackInfo ci) {
        hodgepodge$wasChunkLoadBlocked = ChunkGenScheduler.isBlocked();
        if (hodgepodge$wasChunkLoadBlocked) {
            ChunkGenScheduler.enableChunkLoads();
        }

        final ChunkProviderServer cps = myWorld.theChunkProviderServer;
        hodgepodge$wasLoadChunkOnProvideRequestDisabled = !cps.loadChunkOnProvideRequest;
        if (hodgepodge$wasLoadChunkOnProvideRequestDisabled) {
            cps.loadChunkOnProvideRequest = true;
        }

        // Proactively force-load the chunk at the destination position so that the portal scan
        // and makePortalAt's setBlock() calls operate on real terrain from the start.
        final int cx = ((int) Math.floor(x)) >> 4;
        final int cz = ((int) Math.floor(z)) >> 4;
        cps.loadChunk(cx, cz);
    }

    @Inject(method = "placeInPortal", at = @At("RETURN"))
    private void hodgepodge$reblockAfterPortalSearch(Entity entity, double x, double y, double z, float facing,
            CallbackInfo ci) {
        if (hodgepodge$wasChunkLoadBlocked) {
            ChunkGenScheduler.disableChunkLoads();
        }
        if (hodgepodge$wasLoadChunkOnProvideRequestDisabled) {
            myWorld.theChunkProviderServer.loadChunkOnProvideRequest = false;
        }
    }
}
