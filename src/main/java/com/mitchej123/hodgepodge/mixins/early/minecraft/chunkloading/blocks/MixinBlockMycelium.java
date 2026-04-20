package com.mitchej123.hodgepodge.mixins.early.minecraft.chunkloading.blocks;

import java.util.Random;

import net.minecraft.block.BlockMycelium;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(value = BlockMycelium.class, priority = 800)
public class MixinBlockMycelium {

    /**
     * @author tth05, Alexdoru
     * @reason 1. Prevent loading unloaded chunks when calling getBlock 2. Cache identical block calls 3. Remove unused
     *         getBlock call 4. Give more chance for mycelium to spread if it randomly selects this block 5. Skip
     *         useless getBlock calls
     */
    @Overwrite
    public void updateTick(World worldIn, int x, int y, int z, Random random) {
        if (worldIn.isRemote) return;

        final int offset = 2;
        if (!worldIn.checkChunksExist(x - offset, 0, z - offset, x + offset, 0, z + offset)) {
            return;
        }
        final int lightValue = worldIn.getBlockLightValue(x, y + 1, z);
        if (lightValue < 4 && worldIn.getBlockLightOpacity(x, y + 1, z) > 2) {
            worldIn.setBlock(x, y, z, Blocks.dirt);
        } else if (lightValue >= 9) {
            for (int i = 0; i < 4; ++i) {
                final int newX = x + random.nextInt(3) - 1;
                final int newY = y + random.nextInt(5) - 3;
                final int newZ = z + random.nextInt(3) - 1;
                if (newX == x && newZ == z && (newY == y || newY == y - 1)) {
                    i--; // give another chance for mycelium to spread
                    continue;
                }
                if (worldIn.getBlock(newX, newY, newZ) == Blocks.dirt && worldIn.getBlockMetadata(newX, newY, newZ) == 0
                        && worldIn.getBlockLightOpacity(newX, newY + 1, newZ) <= 2
                        && worldIn.getBlockLightValue(newX, newY + 1, newZ) >= 4) {
                    worldIn.setBlock(newX, newY, newZ, Blocks.mycelium);
                }
            }
        }
    }
}
