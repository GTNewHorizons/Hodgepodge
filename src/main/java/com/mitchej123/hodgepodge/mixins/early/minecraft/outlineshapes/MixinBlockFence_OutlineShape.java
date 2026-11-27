package com.mitchej123.hodgepodge.mixins.early.minecraft.outlineshapes;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFence;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;

import com.gtnewhorizon.gtnhlib.api.IBlockWithOutlineShape;
import com.gtnewhorizon.gtnhlib.api.OutlineShape;

@Mixin(BlockFence.class)
public abstract class MixinBlockFence_OutlineShape implements IBlockWithOutlineShape {

    @Override
    public OutlineShape getOutlineShape(World world, int x, int y, int z) {
        List<AxisAlignedBB> collision = new ArrayList<>(2);
        AxisAlignedBB mask = AxisAlignedBB.getBoundingBox(x, y, z, x + 1, y + 1, z + 1);
        ((Block) (Object) this).addCollisionBoxesToList(world, x, y, z, mask, collision, null);
        List<AxisAlignedBB> boxes = new ArrayList<>(collision.size());
        for (AxisAlignedBB box : collision) {
            boxes.add(
                    AxisAlignedBB.getBoundingBox(
                            box.minX - x,
                            box.minY - y,
                            box.minZ - z,
                            box.maxX - x,
                            Math.min(box.maxY - y, 1),
                            box.maxZ - z));
        }
        return OutlineShape.of(boxes);
    }
}
