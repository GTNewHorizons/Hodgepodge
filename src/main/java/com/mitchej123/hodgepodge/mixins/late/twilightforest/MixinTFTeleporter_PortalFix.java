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
 * Fixes sky-spawn on first TF portal entry when chunk throttling is active.
 *
 * <p>
 * Under throttle, {@code ChunkProviderServer.provideChunk()} returns {@code EmptyChunk}, so {@code placeInExistingPortal}'s
 * spiral scan finds nothing and {@code makePortal}'s terrain scan sees all-air. The fallback places the portal at
 * {@code overworldY * 0.5} via {@code setBlock()} calls that are silently discarded into empty chunks, leaving the
 * player at raw translated coordinates above the canopy.
 *
 * <p>
 * A temporary unblock is insufficient: TF multi-chunk structures (Hollow Hills, Lich Towers, etc.) trigger cascading
 * population across chunk borders, which {@link com.mitchej123.hodgepodge.mixins.early.minecraft.fastload.MixinChunkProviderServer_DeferPopulation}
 * defers, and {@link com.mitchej123.hodgepodge.mixins.early.minecraft.chunkloading.MixinWorldServer_PreventChunkLoading}
 * may re-suppress {@code loadChunkOnProvideRequest} mid-population.
 *
 * <p>
 * Fix: permanently exclude the TF dimension via {@link ChunkGenScheduler#excludeDimFromChunkThrottle(int)} in the
 * {@code <init>} injection, bypassing EntityGuard, chunk-load suppression, and deferred population for that dim.
 * The HEAD/RETURN injections on {@code placeInPortal} additionally unblock the global scheduler flag and
 * pre-load the destination chunk as a belt-and-suspenders measure.
 */
@Mixin(value = TFTeleporter.class, remap = false)
public class MixinTFTeleporter_PortalFix {

    @Shadow(remap = false)
    protected WorldServer myWorld;

    @Unique
    private boolean hodgepodge$wasChunkLoadBlocked;

    @Unique
    private boolean hodgepodge$wasLoadChunkOnProvideRequestDisabled;

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
