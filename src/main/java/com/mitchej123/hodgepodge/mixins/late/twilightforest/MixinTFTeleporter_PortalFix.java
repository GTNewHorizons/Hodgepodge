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
 * Fixes players spawning in the sky (or inside terrain) when entering the Twilight Forest portal for the first time in
 * a freshly-generated region.
 *
 * <h3>Root cause</h3>
 *
 * <p>
 * When a player steps through a Twilight Forest portal, Forge calls
 * {@code ServerConfigurationManager.transferEntityToWorld(..., TFTeleporter)}, which calls
 * {@code TFTeleporter.placeInPortal}. The teleporter first tries {@code placeInExistingPortal}: it spirals outward up
 * to 200 blocks scanning columns for {@code TFBlocks.portal} via {@code world.getBlock()}, which goes through
 * {@code ChunkProviderServer.provideChunk()}. Hodgepodge's EntityGuard mixin returns {@code EmptyChunk} for unloaded
 * chunks while chunk loading is throttled. With all chunks appearing empty, the scan finds no portal block and falls
 * through.
 *
 * <p>
 * {@code makePortal} is then called: it scans for suitable terrain via {@code world.isAirBlock()} /
 * {@code world.getBlock()}, which again hit only empty chunks. With every block appearing to be air,
 * {@code findPortalCoords} cannot identify a valid grass surface and returns {@code null}. The fallback branch then
 * calls {@code makePortalAt} with a raw scaled Y (overworld Y × 0.5), placing the portal structure near sky-level in
 * the TF world — but since the target chunk is still {@code EmptyChunk}, every {@code setBlock()} call is silently
 * discarded. The follow-up {@code placeInExistingPortal} call finds no portal (nothing was written), leaving the entity
 * at its raw translated coordinates. Because the overworld Y is preserved unchanged and TF terrain peaks around
 * Y=60–70, a player entering from an overworld position above Y=70 ends up floating above the forest canopy — i.e.
 * <em>spawning in the sky</em>.
 *
 * <p>
 * A simple temporary chunk-load unblock (setting {@code chunkLoadBlocked = false} and
 * {@code loadChunkOnProvideRequest = true} only for the duration of {@code placeInPortal}) is not sufficient. TF
 * generates large multi-chunk structures — Hollow Hills, Hedge Mazes, Lich Towers, etc. — that write blocks into
 * neighbouring chunks during population. When chunk A is being populated ({@code populationDepth = 1}) and its
 * structure generator accesses chunk B, Hodgepodge's
 * {@link com.mitchej123.hodgepodge.mixins.early.minecraft.fastload.MixinChunkProviderServer_DeferPopulation} defers B's
 * population. Within that deferred population call,
 * {@link com.mitchej123.hodgepodge.mixins.early.minecraft.chunkloading.MixinWorldServer_PreventChunkLoading}'s
 * {@code hodgepodge$onUpdateTick} may suppress {@code loadChunkOnProvideRequest} for any {@code Block.updateTick}
 * triggered by population-time block notifications, causing further neighbour reads to return {@code EmptyChunk}. The
 * end result is incomplete structures at chunk borders and, specifically for portals, a scan that cannot find or write
 * the portal blocks.
 *
 * <h3>Fix</h3>
 *
 * <p>
 * The constructor injection permanently adds the Twilight Forest dimension ID to
 * {@link ChunkGenScheduler#excludeDimFromChunkThrottle(int)}, which:
 * <ul>
 * <li>{@link com.mitchej123.hodgepodge.mixins.early.minecraft.fastload.MixinChunkProviderServer_EntityGuard}:
 * {@code provideChunk()} always loads chunks normally (bypasses all guards).</li>
 * <li>{@link com.mitchej123.hodgepodge.mixins.early.minecraft.chunkloading.MixinWorldServer_PreventChunkLoading}:
 * entity/block-update ticking no longer suppresses {@code loadChunkOnProvideRequest} for this dimension.</li>
 * <li>{@link com.mitchej123.hodgepodge.mixins.early.minecraft.fastload.MixinChunkProviderServer_DeferPopulation}:
 * population of chunks in this dimension is never deferred, even when called from within another population pass
 * ({@code populationDepth > 0}). This allows full synchronous cascading population, ensuring TF multi-chunk structures
 * (Hollow Hills, Hedge Mazes, Lich Towers, etc.) are always complete at chunk borders.</li>
 * </ul>
 *
 * <p>
 * The {@code placeInPortal} HEAD/RETURN injections are retained as belt-and-suspenders: they additionally unblock the
 * global {@link ChunkGenScheduler} flag and restore {@code loadChunkOnProvideRequest}, and proactively force-load the
 * entity's destination chunk before the scan. For excluded dimensions these are largely redundant but harmless.
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
     * instance (i.e. when the TF world is first accessed), permanently adding the dimension ID to the exclusion set so
     * that all subsequent chunk operations in that dimension bypass the throttle guards.
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
