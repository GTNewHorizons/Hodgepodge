package com.mitchej123.hodgepodge.mixins.early.minecraft.outlineshapes;

import static net.minecraft.util.AxisAlignedBB.getBoundingBox;

import net.minecraft.block.BlockEndPortalFrame;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;

import com.gtnewhorizon.gtnhlib.api.IBlockWithOutlineShape;
import com.gtnewhorizon.gtnhlib.api.OutlineShape;

@Mixin(BlockEndPortalFrame.class)
public abstract class MixinBlockEndPortalFrame_OutlineShape implements IBlockWithOutlineShape {

    private static final OutlineShape hodgepodge$FRAME = OutlineShape.of(getBoundingBox(0, 0, 0, 1, 0.8125, 1));
    private static final OutlineShape hodgepodge$FRAME_WITH_EYE = OutlineShape
            .of(getBoundingBox(0, 0, 0, 1, 0.8125, 1), getBoundingBox(0.25, 0.8125, 0.25, 0.75, 1, 0.75));

    @Override
    public OutlineShape getOutlineShape(World world, int x, int y, int z) {
        return (world.getBlockMetadata(x, y, z) & 4) != 0 ? hodgepodge$FRAME_WITH_EYE : hodgepodge$FRAME;
    }
}
