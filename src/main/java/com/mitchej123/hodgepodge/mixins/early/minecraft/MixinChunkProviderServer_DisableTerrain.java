package com.mitchej123.hodgepodge.mixins.early.minecraft;

import java.util.Arrays;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkProviderServer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ChunkProviderServer.class)
public class MixinChunkProviderServer_DisableTerrain {

    @ModifyVariable(
            at = @At(
                    value = "INVOKE_ASSIGN",
                    target = "Lnet/minecraft/world/chunk/IChunkProvider;provideChunk(II)Lnet/minecraft/world/chunk/Chunk;"),
            method = "originalLoadChunk")
    public Chunk hodgepodge$disableTerrain(Chunk chunk) {
        final Block[] ids = new Block[65536];
        final byte[] metadata = new byte[65536];
        Arrays.fill(ids, Blocks.air);

        byte[] chunkData = chunk.getBiomeArray();
        Chunk newChunk = new Chunk(chunk.worldObj, ids, metadata, chunk.xPosition, chunk.zPosition);
        newChunk.setBiomeArray(chunkData);

        return newChunk;
    }
}
