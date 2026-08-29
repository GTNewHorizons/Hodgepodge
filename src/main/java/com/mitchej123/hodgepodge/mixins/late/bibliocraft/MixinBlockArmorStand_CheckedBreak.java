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
    private void checkMatchesBlock(World world, int x, int y, int z, Block blockBroken, int metaData,
            Operation<Void> original) {
        int otherY = metaData < 4 ? y + 1 : y - 1;
        int otherMeta = metaData < 4 ? metaData + 4 : metaData - 4;

        if (world.getBlock(x, otherY, z) == blockBroken && world.getBlockMetadata(x, otherY, z) == otherMeta) {
            original.call(world, x, y, z, blockBroken, metaData);
        } else {
            // If the block below / above it is not the other part of the armor stand, don't break that block.
            if (metaData < 4) {
                this.dropItems(world, x, y, z);
            }

            super.breakBlock(world, x, y, z, blockBroken, metaData);
        }
    }
}
