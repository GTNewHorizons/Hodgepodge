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
 * Fixes wrong spawn position and incomplete maze generation when using a Thaumcraft eldritch portal.
 * <p>
 * The outer-lands dimension is excluded from chunk-load throttling and population deferral via
 * {@link ChunkGenScheduler#excludeDimFromChunkThrottle(int)}: {@code provideChunk} always loads normally, and
 * population is never deferred even when triggered recursively ({@code populationDepth > 0}). The {@code placeInPortal}
 * injections additionally unblock chunk loads and force-populate the entity's destination chunk before the portal scan.
 */
@Mixin(value = TeleporterThaumcraft.class, remap = false)
public class MixinTeleporterThaumcraft_PortalFix {

    @Shadow(remap = false)
    private WorldServer worldServerInstance;

    @Unique
    private boolean hodgepodge$wasChunkLoadBlocked;

    @Unique
    private boolean hodgepodge$wasLoadChunkOnProvideRequestDisabled;

    /** Permanently exclude the outer-lands dimension from chunk-load throttling and population deferral. */
    @Inject(method = "<init>", at = @At("RETURN"))
    private void hodgepodge$registerOuterLandsDim(WorldServer worldServer, CallbackInfo ci) {
        ChunkGenScheduler.excludeDimFromChunkThrottle(worldServer.provider.dimensionId);
    }

    @Inject(method = "placeInPortal", at = @At("HEAD"))
    private void hodgepodge$unblockForPortalSearch(Entity entity, double x, double y, double z, float yaw,
            CallbackInfo ci) {
        hodgepodge$wasChunkLoadBlocked = ChunkGenScheduler.isBlocked();
        if (hodgepodge$wasChunkLoadBlocked) {
            ChunkGenScheduler.enableChunkLoads();
        }

        final ChunkProviderServer cps = worldServerInstance.theChunkProviderServer;
        hodgepodge$wasLoadChunkOnProvideRequestDisabled = !cps.loadChunkOnProvideRequest;
        if (hodgepodge$wasLoadChunkOnProvideRequestDisabled) {
            cps.loadChunkOnProvideRequest = true;
        }

        // Proactively force-load and force-populate the chunk at the entity's destination position so that the
        // outer-lands portal room (including blockEldritchPortal at Y=53) is in place before the scan begins.
        final int cx = ((int) Math.floor(entity.posX)) >> 4;
        final int cz = ((int) Math.floor(entity.posZ)) >> 4;
        cps.loadChunk(cx, cz);
        cps.populate(cps, cx, cz);
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
