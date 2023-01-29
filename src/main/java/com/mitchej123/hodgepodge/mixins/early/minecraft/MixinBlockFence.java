package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.material.Material;
import net.minecraft.world.IBlockAccess;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(BlockFence.class)
public class MixinBlockFence {

    /**
     * @author mitchej123 & leagris
     * @reason Fix fence connections with extra fences, etc
     */
    @Overwrite
    public boolean canConnectFenceTo(IBlockAccess world, int x, int y, int z) {
        Block block = world.getBlock(x, y, z);
        return block == (BlockFence) (Object) this
                || block instanceof BlockFence && block.getMaterial() == ((BlockFence) (Object) this).getMaterial()
                || block instanceof BlockFenceGate
                || block.getMaterial().isOpaque() && block.renderAsNormalBlock()
                        && block.getMaterial() != Material.gourd;
    }
}
