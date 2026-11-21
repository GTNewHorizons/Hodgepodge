package com.mitchej123.hodgepodge.mixins.early.minecraft.outlineshapes;

import static net.minecraft.util.AxisAlignedBB.getBoundingBox;

import java.util.Arrays;
import java.util.List;

import net.minecraft.block.BlockHopper;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.gtnewhorizon.gtnhlib.api.IBlockWithOutlineShape;
import com.gtnewhorizon.gtnhlib.api.OutlineShape;

@Mixin(BlockHopper.class)
public abstract class MixinBlockHopper_OutlineShape implements IBlockWithOutlineShape {

    private static final AxisAlignedBB[][] hodgepodge$PARTS = {
            hodgepodge$parts(getBoundingBox(0.375, 0, 0.375, 0.625, 0.25, 0.625)), hodgepodge$parts(null),
            hodgepodge$parts(getBoundingBox(0.375, 0.25, 0, 0.625, 0.5, 0.25)),
            hodgepodge$parts(getBoundingBox(0.375, 0.25, 0.75, 0.625, 0.5, 1)),
            hodgepodge$parts(getBoundingBox(0, 0.25, 0.375, 0.25, 0.5, 0.625)),
            hodgepodge$parts(getBoundingBox(0.75, 0.25, 0.375, 1, 0.5, 0.625)) };
    private static final OutlineShape[] hodgepodge$SHAPES = new OutlineShape[hodgepodge$PARTS.length];

    static {
        for (int i = 0; i < hodgepodge$PARTS.length; i++) hodgepodge$SHAPES[i] = OutlineShape.of(hodgepodge$PARTS[i]);
    }

    private static AxisAlignedBB[] hodgepodge$parts(AxisAlignedBB spout) {
        AxisAlignedBB[] parts = { getBoundingBox(0.125, 0.625, 0.125, 0.875, 0.625, 0.875),
                getBoundingBox(0, 0.625, 0, 0.125, 1, 1), getBoundingBox(0.875, 0.625, 0, 1, 1, 1),
                getBoundingBox(0, 0.625, 0, 1, 1, 0.125), getBoundingBox(0, 0.625, 0.875, 1, 1, 1),
                getBoundingBox(0.25, 0.25, 0.25, 0.75, 0.625, 0.75), spout };
        return spout != null ? parts : Arrays.copyOf(parts, parts.length - 1);
    }

    private static int hodgepodge$facing(World world, int x, int y, int z) {
        int facing = BlockHopper.getDirectionFromMetadata(world.getBlockMetadata(x, y, z));
        return facing < hodgepodge$PARTS.length ? facing : 1;
    }

    @Override
    public OutlineShape getOutlineShape(World world, int x, int y, int z) {
        return hodgepodge$SHAPES[hodgepodge$facing(world, x, y, z)];
    }

    @Inject(method = "addCollisionBoxesToList", at = @At("HEAD"), cancellable = true)
    @SuppressWarnings("unchecked")
    private void hodgepodge$collideWithParts(World world, int x, int y, int z, AxisAlignedBB mask, List list,
            Entity entity, CallbackInfo ci) {
        for (AxisAlignedBB part : hodgepodge$PARTS[hodgepodge$facing(world, x, y, z)]) {
            AxisAlignedBB placed = part.getOffsetBoundingBox(x, y, z);
            if (mask.intersectsWith(placed)) list.add(placed);
        }
        ci.cancel();
    }
}
