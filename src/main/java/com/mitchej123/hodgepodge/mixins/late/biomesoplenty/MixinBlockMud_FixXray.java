package com.mitchej123.hodgepodge.mixins.late.biomesoplenty;

import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;

import com.mitchej123.hodgepodge.mixins.early.minecraft.MixinBlock_FixXray;

import biomesoplenty.common.blocks.BlockMud;

/**
 * Blocks that are rendered as full blocks but override the
 * {@link net.minecraft.block.Block#getCollisionBoundingBoxFromPool(World, int, int, int)} method to return null should
 * be added to this mixin so that the player can't look through them in F5 and have xray vision
 *
 * @author Alexdoru
 */
@Mixin(BlockMud.class)
public abstract class MixinBlockMud_FixXray extends MixinBlock_FixXray {

    @Override
    public boolean hodgepodge$shouldRayTraceStopOnBlock(World world, int x, int y, int z) {
        return true;
    }

}
