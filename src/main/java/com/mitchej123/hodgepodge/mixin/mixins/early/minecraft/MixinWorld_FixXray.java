package com.mitchej123.hodgepodge.mixin.mixins.early.minecraft;

import net.minecraft.block.Block;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.mitchej123.hodgepodge.mixin.ducks.BlockDuck_FixXray;

@Mixin(World.class)
public class MixinWorld_FixXray {

    @Unique
    private static final AxisAlignedBB hodgepodge$DUMMY_AABB = AxisAlignedBB.getBoundingBox(0, 0, 0, 0, 0, 0);

    @Redirect(
            method = "func_147447_a",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/Block;getCollisionBoundingBoxFromPool(Lnet/minecraft/world/World;III)Lnet/minecraft/util/AxisAlignedBB;"))
    private AxisAlignedBB hodgepodge$fixXray(Block block, World world, int x, int y, int z) {
        if (((BlockDuck_FixXray) block).hodgepodge$shouldRayTraceStopOnBlock(world, x, y, z)) {
            return hodgepodge$DUMMY_AABB;
        }
        return null;
    }

}
