package com.mitchej123.hodgepodge.mixins.early.minecraft.outlineshapes;

import net.minecraft.block.BlockDragonEgg;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;

import com.gtnewhorizon.gtnhlib.api.IBlockWithOutlineShape;
import com.gtnewhorizon.gtnhlib.api.OutlineShape;

@Mixin(BlockDragonEgg.class)
public abstract class MixinBlockDragonEgg_OutlineShape implements IBlockWithOutlineShape {

    private static final OutlineShape hodgepodge$SHAPE = OutlineShape.of(
            hodgepodge$layer(0, 1, 3),
            hodgepodge$layer(1, 3, 6),
            hodgepodge$layer(3, 8, 7),
            hodgepodge$layer(8, 11, 6),
            hodgepodge$layer(11, 13, 5),
            hodgepodge$layer(13, 14, 4),
            hodgepodge$layer(14, 15, 3),
            hodgepodge$layer(15, 16, 2));

    private static AxisAlignedBB hodgepodge$layer(int fromY, int toY, int halfWidth) {
        double min = (8 - halfWidth) / 16.0, max = (8 + halfWidth) / 16.0;
        return AxisAlignedBB.getBoundingBox(min, fromY / 16.0, min, max, toY / 16.0, max);
    }

    @Override
    public OutlineShape getOutlineShape(World world, int x, int y, int z) {
        return hodgepodge$SHAPE;
    }
}
