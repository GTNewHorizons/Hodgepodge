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
 * Fixes sky-spawn on first TF portal entry under chunk throttling. Under throttle, {@code provideChunk()} returns
 * {@code EmptyChunk}, causing portal scans to fail and {@code setBlock()} calls to be discarded, leaving the player at
 * raw translated coordinates. A temporary unblock is insufficient due to cascading cross-chunk population in TF
 * structures. Fix: permanently exclude the TF dim via {@link ChunkGenScheduler#excludeDimFromChunkThrottle(int)};
 * HEAD/RETURN hooks on {@code placeInPortal} additionally unblock the global flag and pre-load the destination chunk.
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
