package com.mitchej123.hodgepodge.mixins.early.minecraft.fastload;

import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkProviderServer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.mitchej123.hodgepodge.mixins.hooks.ChunkGenScheduler;

/**
 * Bukkit variant of EntityGuard.
 */
@Mixin(ChunkProviderServer.class)
public class MixinChunkProviderServer_EntityGuard_Bukkit {

    @Shadow
    private Chunk defaultEmptyChunk;

    @Shadow
    public WorldServer worldObj;

    @Inject(method = "provideChunk(II)Lnet/minecraft/world/chunk/Chunk;", at = @At("HEAD"), cancellable = true)
    private void hodgepodge$blockDuringEntityTicks(int x, int z, CallbackInfoReturnable<Chunk> cir) {
        if (!ChunkGenScheduler.isBlocked() || this.worldObj.findingSpawnPoint) return;

        // Let already-loaded chunks through to Thermos's full provideChunk logic
        ChunkProviderServer self = (ChunkProviderServer) (Object) this;
        if (self.chunkExists(x, z)) return;

        // Block: return empty chunk for unloaded chunks during entity ticks
        ChunkGenScheduler.incrementBlockedLoadCount();
        cir.setReturnValue(this.defaultEmptyChunk);
    }
}
