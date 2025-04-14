package com.mitchej123.hodgepodge.mixins.early.minecraft;

import com.mitchej123.hodgepodge.ISimulationDistanceWorldServer;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


/**
 * Must run after MixinWorldClient_FixAllocations
 */
@Mixin(value = Chunk.class, priority = 1001)
public abstract class MixinChunk_SimulationDistance {
    @Inject(method = "onChunkUnload", at = @At("HEAD"))
    void hodgepodge$chunkUnloaded(CallbackInfo ci) {
        Chunk chunk = (Chunk) (Object) this;
        if (chunk.worldObj instanceof WorldServer) {
            long pos = ChunkCoordIntPair.chunkXZ2Int(chunk.xPosition, chunk.zPosition);
            ((ISimulationDistanceWorldServer) chunk.worldObj).hodgepodge$chunkUnloaded(pos);
        }
    }
}
