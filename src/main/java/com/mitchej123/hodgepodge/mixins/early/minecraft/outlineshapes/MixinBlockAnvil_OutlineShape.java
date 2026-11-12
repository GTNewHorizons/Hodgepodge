package com.mitchej123.hodgepodge.mixins.early.minecraft.outlineshapes;

import static net.minecraft.util.AxisAlignedBB.getBoundingBox;

import net.minecraft.block.BlockAnvil;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;

import com.gtnewhorizon.gtnhlib.api.IBlockWithOutlineShape;
import com.gtnewhorizon.gtnhlib.api.OutlineShape;

@Mixin(BlockAnvil.class)
public abstract class MixinBlockAnvil_OutlineShape implements IBlockWithOutlineShape {

    private static final OutlineShape hodgepodge$ALONG_Z = OutlineShape.of(
            getBoundingBox(0.125, 0, 0.125, 0.875, 0.25, 0.875),
            getBoundingBox(0.25, 0.25, 0.1875, 0.75, 0.3125, 0.8125),
            getBoundingBox(0.375, 0.3125, 0.25, 0.625, 0.625, 0.75),
            getBoundingBox(0.1875, 0.625, 0, 0.8125, 1, 1));
    private static final OutlineShape hodgepodge$ALONG_X = OutlineShape.of(
            getBoundingBox(0.125, 0, 0.125, 0.875, 0.25, 0.875),
            getBoundingBox(0.1875, 0.25, 0.25, 0.8125, 0.3125, 0.75),
            getBoundingBox(0.25, 0.3125, 0.375, 0.75, 0.625, 0.625),
            getBoundingBox(0, 0.625, 0.1875, 1, 1, 0.8125));

    @Override
    public OutlineShape getOutlineShape(World world, int x, int y, int z) {
        int facing = world.getBlockMetadata(x, y, z) & 3;
        return facing == 1 || facing == 3 ? hodgepodge$ALONG_X : hodgepodge$ALONG_Z;
    }
}
