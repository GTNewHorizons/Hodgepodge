package com.mitchej123.hodgepodge.mixins.late.biomesoplenty;

import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;

import com.mitchej123.hodgepodge.mixins.early.minecraft.MixinBlock_FixXray;

import biomesoplenty.common.blocks.BlockMud;

@Mixin(BlockMud.class)
public abstract class MixinBlockMud extends MixinBlock_FixXray {

    @Override
    public boolean hodgepodge$shouldRayTraceStopOnBlock(World world, int x, int y, int z) {
        return true;
    }

}
