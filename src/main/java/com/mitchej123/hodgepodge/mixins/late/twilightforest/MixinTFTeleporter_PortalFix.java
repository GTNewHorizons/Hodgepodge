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
 * Temporarily lift all chunk-load guards for the duration of {@code placeInPortal}, mirroring the vanilla
 * {@link com.mitchej123.hodgepodge.mixins.early.minecraft.MixinTeleporter_PortalFix Nether portal fix}:
 * <ul>
 * <li>Unblock the global {@link ChunkGenScheduler} flag so EntityGuard's first check passes.</li>
 * <li>Set {@code loadChunkOnProvideRequest = true} on the target world's chunk provider so that {@code provideChunk()}
 * falls through to a real {@code loadChunk()} call.</li>
 * <li>Pre-load the chunk at the entity's destination so that the very first {@code getBlock()} / {@code setBlock()}
 * hits real terrain rather than the empty chunk.</li>
 * </ul>
 * Both flags are restored to their previous values on {@code RETURN}. A permanent per-dimension exclusion is
 * intentionally avoided here: unlike the Thaumcraft outer-lands maze, TF portal placement does not require cascading
 * synchronous population of neighbouring chunks, so a permanent bypass would only add unnecessary load.
 */
@Mixin(value = TFTeleporter.class, remap = false)
public class MixinTFTeleporter_PortalFix {

    @Shadow(remap = false)
    protected WorldServer myWorld;

    @Unique
    private boolean hodgepodge$wasChunkLoadBlocked;

    @Unique
    private boolean hodgepodge$wasLoadChunkOnProvideRequestDisabled;

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
