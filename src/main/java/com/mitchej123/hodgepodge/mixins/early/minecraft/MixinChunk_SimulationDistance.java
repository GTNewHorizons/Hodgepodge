package com.mitchej123.hodgepodge.mixins.early.minecraft;

import com.mitchej123.hodgepodge.ISimulationDistanceWorld;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Chunk.class)
public abstract class MixinChunk_SimulationDistance {
    @Inject(method = "onChunkUnload", at = @At("HEAD"))
    void hodgepodge$chunkUnloaded(CallbackInfo ci) {
        Chunk chunk = (Chunk) (Object) this;
        if (chunk.worldObj instanceof WorldServer) {
            long pos = ChunkCoordIntPair.chunkXZ2Int(chunk.xPosition, chunk.zPosition);
            ((ISimulationDistanceWorld) chunk.worldObj).hodgepodge$getSimulationDistanceHelper().chunkUnloaded(pos);
        }
    }
}
