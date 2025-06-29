package com.mitchej123.hodgepodge.mixins.late.harvestthenether;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLeavesBase;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.world.IBlockAccess;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import com.pam.harvestthenether.BlockNetherLeaves;
import com.pam.harvestthenether.BlockRegistry;

@Mixin(BlockNetherLeaves.class)
public class MixinBlockNetherLeaves extends BlockLeavesBase {

    private MixinBlockNetherLeaves(Material p_i45433_1_, boolean p_i45433_2_) {
        super(p_i45433_1_, p_i45433_2_);
    }

    @Override
    public boolean shouldSideBeRendered(IBlockAccess worldIn, int x, int y, int z, int side) {
        return (Minecraft.isFancyGraphicsEnabled() || worldIn.getBlock(x, y, z) != this)
                && (side == 0 && this.minY > 0.0D || (side == 1 && this.maxY < 1.0D || (side == 2 && this.minZ > 0.0D
                        || (side == 3 && this.maxZ < 1.0D || (side == 4 && this.minX > 0.0D
                                || (side == 5 && this.maxX < 1.0D || hodgepodge$isTranslucent(worldIn, x, y, z)))))));
    }

    @Unique
    private static boolean hodgepodge$isTranslucent(IBlockAccess world, int x, int y, int z) {
        Block block = world.getBlock(x, y, z);
        return block == BlockRegistry.netherLeaves ? Minecraft.isFancyGraphicsEnabled() : !block.isOpaqueCube();
    }
}
