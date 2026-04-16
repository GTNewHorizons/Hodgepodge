package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.entity.Entity;
import net.minecraft.world.Teleporter;
import net.minecraft.world.WorldServer;
import net.minecraft.world.gen.ChunkProviderServer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mitchej123.hodgepodge.mixins.hooks.ChunkGenScheduler;

/**
 * Fixes players spawning inside blocks (e.g. stuck in netherrack) when entering a vanilla Nether or End portal for the
 * first time in a freshly-generated region.
 *
 * <h3>Root cause</h3>
 *
 * <p>
 * When a player steps through a portal, Forge calls
 * {@code ServerConfigurationManager.transferEntityToWorld(..., Teleporter)}, which in turn calls
 * {@code Teleporter.placeInPortal}. The teleporter searches for an existing portal frame, and if none is found, calls
 * {@code makePortal} to build one.
 *
 * <p>
 * Both {@code placeInExistingPortal} and {@code makePortal} issue many {@code world.getBlock()} / {@code setBlock()}
 * calls across a wide radius. These go through {@code ChunkProviderServer.provideChunk()}, which is intercepted by
 * Hodgepodge's EntityGuard mixin. When chunk loading is being throttled (i.e.
 * {@code SpeedupsConfig.preventEntityChunkLoading} / {@code throttleChunkGeneration} /
 * {@code FixesConfig.preventChunkLoadingFromBlockUpdates} is active), the guard returns {@code EmptyChunk} for unloaded
 * chunks instead of generating them.
 *
 * <p>
 * With empty chunks standing in for real terrain:
 * <ul>
 * <li>{@code placeInExistingPortal} finds no portal blocks → returns {@code false}.</li>
 * <li>{@code makePortal} treats every column as "all air" (EmptyChunk) and cannot find a suitable solid floor → falls
 * through to its forced-placement branch at y=70 and calls {@code setBlock} to build the obsidian frame and portal
 * blocks. These {@code setBlock} calls land in the EmptyChunk and are silently discarded.</li>
 * <li>The second {@code placeInExistingPortal} call finds nothing → player is left at their translated position, which
 * is inside solid netherrack / end stone.</li>
 * </ul>
 *
 * <h3>Fix</h3>
 *
 * <p>
 * Temporarily lift all chunk-load guards for the duration of {@code placeInPortal}:
 * <ul>
 * <li>Unblock the global {@link ChunkGenScheduler} flag so EntityGuard's first check passes.</li>
 * <li>Set {@code loadChunkOnProvideRequest = true} on the target world's chunk provider so that {@code provideChunk()}
 * falls through to a real {@code loadChunk()} call.</li>
 * <li>Pre-load the chunk at the entity's destination so that the very first {@code getBlock()} / {@code setBlock()}
 * hits real terrain rather than the empty chunk.</li>
 * </ul>
 * Both flags are restored to their previous values on {@code RETURN}.
 *
 * <p>
 * This fix applies to the vanilla {@link Teleporter} (Nether portals, End platform). Mod-specific teleporters that
 * override {@code placeInPortal} without calling {@code super} are handled separately (see
 * {@code MixinTeleporterThaumcraft_PortalFix} for the Thaumcraft outer-lands case).
 */
@Mixin(Teleporter.class)
public class MixinTeleporter_PortalFix {

    @Shadow
    private WorldServer worldServerInstance;

    @Unique
    private boolean hodgepodge$wasChunkLoadBlocked;

    @Unique
    private boolean hodgepodge$wasLoadChunkOnProvideRequestDisabled;

    @Inject(method = "placeInPortal", at = @At("HEAD"))
    private void hodgepodge$unblockForPortalPlacement(Entity entity, double x, double y, double z, float yaw,
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

        // Pre-load the chunk at the entity's destination position so that the portal scan and makePortal's
        // setBlock() calls operate on real terrain from the start.
        final int cx = ((int) Math.floor(entity.posX)) >> 4;
        final int cz = ((int) Math.floor(entity.posZ)) >> 4;
        cps.loadChunk(cx, cz);
    }

    @Inject(method = "placeInPortal", at = @At("RETURN"))
    private void hodgepodge$restoreAfterPortalPlacement(Entity entity, double x, double y, double z, float yaw,
            CallbackInfo ci) {
        if (hodgepodge$wasChunkLoadBlocked) {
            ChunkGenScheduler.disableChunkLoads();
        }
        if (hodgepodge$wasLoadChunkOnProvideRequestDisabled) {
            worldServerInstance.theChunkProviderServer.loadChunkOnProvideRequest = false;
        }
    }
}
