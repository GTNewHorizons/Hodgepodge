package com.mitchej123.hodgepodge.mixins.late.thaumcraft;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRotatedPillar;
import net.minecraft.block.material.Material;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;

import thaumcraft.common.blocks.BlockMagicalLog;

@Mixin(value = BlockMagicalLog.class)
public abstract class MixinBlockMagicalLog extends BlockRotatedPillar {

    protected MixinBlockMagicalLog(Material p_i45425_1_) {
        super(p_i45425_1_);
    }

    /**
     * Old breakBlock() does not override the correct method.
     */
    @Override
    public void breakBlock(World worldIn, int x, int y, int z, Block blockBroken, int meta) {
        byte b0 = 4;
        int i1 = b0 + 1;

        if (worldIn.checkChunksExist(x - i1, y - i1, z - i1, x + i1, y + i1, z + i1)) {
            for (int j1 = -b0; j1 <= b0; ++j1) {
                for (int k1 = -b0; k1 <= b0; ++k1) {
                    for (int l1 = -b0; l1 <= b0; ++l1) {
                        Block block = worldIn.getBlock(x + j1, y + k1, z + l1);
                        if (block.isLeaves(worldIn, x + j1, y + k1, z + l1)) {
                            block.beginLeavesDecay(worldIn, x + j1, y + k1, z + l1);
                        }
                    }
                }
            }
        }
    }
}
