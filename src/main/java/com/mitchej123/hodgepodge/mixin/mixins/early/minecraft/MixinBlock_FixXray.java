package com.mitchej123.hodgepodge.mixin.mixins.early.minecraft;

import net.minecraft.block.Block;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import com.mitchej123.hodgepodge.mixin.ducks.BlockDuck_FixXray;

@Mixin(Block.class)
public abstract class MixinBlock_FixXray implements BlockDuck_FixXray {

    @Shadow
    public abstract AxisAlignedBB getCollisionBoundingBoxFromPool(World worldIn, int x, int y, int z);

    public boolean hodgepodge$shouldRayTraceStopOnBlock(World worldIn, int x, int y, int z) {
        return this.getCollisionBoundingBoxFromPool(worldIn, x, y, z) != null;
    }

}
