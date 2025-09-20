package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.block.Block;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.EmptyChunk;
import net.minecraftforge.common.util.ForgeDirection;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Block.class)
public class MixinBlock_SideFacingUnloadedChunk {

    @Inject(method = "shouldSideBeRendered", at = @At("HEAD"), cancellable = true)
    private void shouldSideBeRenderedFix(IBlockAccess blockAccess, int x, int y, int z, int side,
            CallbackInfoReturnable<Boolean> cir) {
        ForgeDirection direction = ForgeDirection.getOrientation(side);
        if (direction == ForgeDirection.UP || direction == ForgeDirection.DOWN || y < 0 || y > 255) {
            return;
        }

        x += direction.offsetX;
        z += direction.offsetZ;
        x >>= 4;
        z >>= 4;

        if (blockAccess instanceof ChunkCache) {
            MixinChunkCache_SideFacingUnloaded cache = (MixinChunkCache_SideFacingUnloaded) blockAccess;
            Chunk[][] chunks = cache.getChunkArray();

            x -= cache.getChunkX();
            z -= cache.getChunkZ();

            if (x < 0 || x >= chunks.length || z < 0 || z >= chunks[x].length) {
                cir.setReturnValue(false);
            } else if (chunks[x][z] instanceof EmptyChunk) {
                cir.setReturnValue(false);
            }
        }
    }
}
