package com.mitchej123.hodgepodge.mixins.late.bibliocraft;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;

import jds.bibliocraft.blocks.BlockArmorStand;

@Mixin(BlockArmorStand.class)
public abstract class MixinBlockArmorStand_CheckedBreak extends BlockContainer {

    @Shadow(remap = false)
    protected abstract void dropItems(World world, int i, int j, int k);

    protected MixinBlockArmorStand_CheckedBreak(Material p_i45386_1_) {
        super(p_i45386_1_);
    }

    @WrapMethod(method = "breakBlock")
    private void checkMatchesBlock(World world, int x, int y, int z, Block blockBroken, int meta,
            Operation<Void> original) {
        // For the Armor Stand, metadata 0-3 is the bottom half & metadata 4-7 is the top half.
        // (metadata & 0b11) determines the rotation of the block.
        boolean isBottomHalf = meta < 4;

        // Calculate the expected position & meta of the other half of the Armor Stand.
        int otherY = isBottomHalf ? y + 1 : y - 1;
        int otherMeta = isBottomHalf ? meta + 4 : meta - 4;

        if (world.getBlock(x, otherY, z) == blockBroken && world.getBlockMetadata(x, otherY, z) == otherMeta) {
            // If both halves of the Armor Stand are present, destroy both.
            original.call(world, x, y, z, blockBroken, meta);
        } else {
            // If the block below / above it is not the other half of the armor stand, don't break it.
            if (isBottomHalf) {
                // The bottom half of the armor stand stores the inventory.
                this.dropItems(world, x, y, z);
            }

            super.breakBlock(world, x, y, z, blockBroken, meta);
        }
    }
}
