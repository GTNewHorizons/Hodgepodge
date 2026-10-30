package com.mitchej123.hodgepodge.mixins.early.minecraft.fastload;

import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.AnvilChunkLoader;
import net.minecraft.world.chunk.storage.IChunkLoader;
import net.minecraft.world.gen.ChunkProviderServer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mitchej123.hodgepodge.mixins.hooks.ChunkGenScheduler;

/**
 * Bukkit variant of EntityGuard. Wraps the loadChunk call so loaded chunks skip the check.
 */
@Mixin(ChunkProviderServer.class)
public class MixinChunkProviderServer_EntityGuard_Bukkit {

    @Shadow
    private Chunk defaultEmptyChunk;

    @Shadow
    public WorldServer worldObj;

    @Shadow
    public IChunkLoader currentChunkLoader;

    @WrapOperation(
            method = "provideChunk(II)Lnet/minecraft/world/chunk/Chunk;",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/gen/ChunkProviderServer;loadChunk(II)Lnet/minecraft/world/chunk/Chunk;"))
    private Chunk hodgepodge$blockDuringEntityTicks(ChunkProviderServer self, int x, int z, Operation<Chunk> original) {
        if (ChunkGenScheduler.isBlocked() && !this.worldObj.findingSpawnPoint) {
            // Allow disk loads (cheap), only block generation (expensive)
            if (!(this.currentChunkLoader instanceof AnvilChunkLoader acl && acl.chunkExists(this.worldObj, x, z))) {
                ChunkGenScheduler.incrementBlockedLoadCount();
                return this.defaultEmptyChunk;
            }
        }
        return original.call(self, x, z);
    }
}
