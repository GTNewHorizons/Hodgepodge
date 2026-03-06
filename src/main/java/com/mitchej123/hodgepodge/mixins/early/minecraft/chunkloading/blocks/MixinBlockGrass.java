package com.mitchej123.hodgepodge.mixins.early.minecraft.chunkloading.blocks;

import java.util.Random;

import net.minecraft.block.BlockGrass;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;

@Mixin(BlockGrass.class)
public class MixinBlockGrass {

    /**
     * @author tth05
     * @reason Small performance improvements. Prevent chunk loading, avoid doing any work on blocks that can't turn to
     *         grass, remove duplicate {@link World#getBlockLightValue(int, int, int)} call.
     */
    @Overwrite
    public void updateTick(World worldIn, int x, int y, int z, Random random) {
        if (worldIn.isRemote) return;

        if (!hodgepodge$canGetBlockLightSafely(worldIn, x, z)) {
            return;
        }
        int blockLightValue = worldIn.getBlockLightValue(x, y + 1, z);
        if (blockLightValue < 4 && worldIn.getBlockLightOpacity(x, y + 1, z) > 2) {
            worldIn.setBlock(x, y, z, Blocks.dirt);
        } else if (blockLightValue >= 9) {
            for (int i = 0; i < 4; ++i) {
                final int targetX = x + random.nextInt(3) - 1;
                final int targetY = y + random.nextInt(5) - 3;
                final int targetZ = z + random.nextInt(3) - 1;

                if (targetX == x && targetZ == z && (targetY == y || targetY == y - 1)) continue;
                if (!worldIn.blockExists(targetX, targetY, targetZ)) continue;

                if (worldIn.getBlock(targetX, targetY, targetZ) == Blocks.dirt
                        && worldIn.getBlockMetadata(targetX, targetY, targetZ) == 0
                        && worldIn.getBlockLightOpacity(targetX, targetY + 1, targetZ) <= 2
                        && hodgepodge$canGetBlockLightSafely(worldIn, targetX, targetZ)
                        && worldIn.getBlockLightValue(targetX, targetY + 1, targetZ) >= 4) {
                    worldIn.setBlock(targetX, targetY, targetZ, Blocks.grass);
                }
            }
        }
    }

    @Unique
    private static boolean hodgepodge$canGetBlockLightSafely(World worldIn, int x, int z) {
        return worldIn.checkChunksExist(x - 1, 0, z - 1, x + 1, 0, z + 1);
    }
}
