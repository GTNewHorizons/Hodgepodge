package com.mitchej123.hodgepodge.mixins.late.thaumcraft;

import net.minecraft.entity.Entity;

import org.spongepowered.asm.mixin.Mixin;
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
 * Root cause: {@code TeleporterThaumcraft.placeInPortal} scans for the eldritch portal block via
 * {@code world.getBlock()}, which goes through {@code provideChunk()}. Hodgepodge's EntityGuard mixin returns
 * {@code EmptyChunk} for unloaded chunks while chunk loading is blocked (during most of the server tick when
 * throttleChunkGeneration is active). This causes the scan to see only air everywhere, find no portal block, and return
 * without repositioning the player — leaving them at their overworld Y coordinate (e.g. Y=64), which is above the maze
 * structure placed at Y=50–63.
 *
 * <p>
 * Fix: temporarily unblock chunk loading during {@code placeInPortal} so the portal-block scan can find (or generate)
 * the structure, then restore the blocked state on return.
 */
@Mixin(value = TeleporterThaumcraft.class, remap = false)
public class MixinTeleporterThaumcraft_PortalFix {

    @Unique
    private boolean hodgepodge$wasChunkLoadBlocked;

    @Inject(method = "placeInPortal", at = @At("HEAD"))
    private void hodgepodge$unblockForPortalSearch(Entity entity, double x, double y, double z, float yaw,
            CallbackInfo ci) {
        hodgepodge$wasChunkLoadBlocked = ChunkGenScheduler.isBlocked();
        if (hodgepodge$wasChunkLoadBlocked) {
            ChunkGenScheduler.enableChunkLoads();
        }
    }

    @Inject(method = "placeInPortal", at = @At("RETURN"))
    private void hodgepodge$reblockAfterPortalSearch(Entity entity, double x, double y, double z, float yaw,
            CallbackInfo ci) {
        if (hodgepodge$wasChunkLoadBlocked) {
            ChunkGenScheduler.disableChunkLoads();
        }
    }
}
