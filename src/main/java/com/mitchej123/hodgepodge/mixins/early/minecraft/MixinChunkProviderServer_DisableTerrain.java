package com.mitchej123.hodgepodge.mixins.early.minecraft;

import java.util.Arrays;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkProviderServer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ChunkProviderServer.class)
public class MixinChunkProviderServer_DisableTerrain {

    @Unique
    private static final int CHUNK_WIDTH = 16;

    @Unique
    private static final int CHUNK_LENGTH = 16;

    @Unique
    private static final int CHUNK_HEIGHT = 256;

    @Unique
    private static final int BLOCKS_TOTAL = CHUNK_LENGTH * CHUNK_WIDTH * CHUNK_HEIGHT;

    @ModifyVariable(
            at = @At(
                    value = "INVOKE_ASSIGN",
                    target = "Lnet/minecraft/world/chunk/IChunkProvider;provideChunk(II)Lnet/minecraft/world/chunk/Chunk;"),
            method = "originalLoadChunk")
    public Chunk hodgepodge$disableTerrain(Chunk chunk) {
        final Block[] ids = new Block[BLOCKS_TOTAL];
        final byte[] metadata = new byte[BLOCKS_TOTAL];
        Arrays.fill(ids, Blocks.air);

        byte[] chunkData = chunk.getBiomeArray();
        Chunk newChunk = new Chunk(chunk.worldObj, ids, metadata, chunk.xPosition, chunk.zPosition);
        newChunk.setBiomeArray(chunkData);

        return newChunk;
    }
}
