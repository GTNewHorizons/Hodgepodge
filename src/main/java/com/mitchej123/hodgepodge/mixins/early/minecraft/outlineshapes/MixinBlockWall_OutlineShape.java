package com.mitchej123.hodgepodge.mixins.early.minecraft.outlineshapes;

import static net.minecraft.util.AxisAlignedBB.getBoundingBox;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockWall;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import com.gtnewhorizon.gtnhlib.api.IBlockWithOutlineShape;
import com.gtnewhorizon.gtnhlib.api.OutlineShape;

@Mixin(BlockWall.class)
public abstract class MixinBlockWall_OutlineShape extends Block implements IBlockWithOutlineShape {

    private MixinBlockWall_OutlineShape() {
        super(null);
    }

    @Shadow
    public abstract boolean canConnectWallTo(IBlockAccess world, int x, int y, int z);

    @Override
    public OutlineShape getOutlineShape(World world, int x, int y, int z) {
        return OutlineShape.of(hodgepodge$parts(world, x, y, z));
    }

    @Override
    @SuppressWarnings("unchecked")
    public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB mask, List list,
            Entity entity) {
        for (AxisAlignedBB part : hodgepodge$parts(world, x, y, z)) {
            AxisAlignedBB placed = getBoundingBox(
                    x + part.minX,
                    y + part.minY,
                    z + part.minZ,
                    x + part.maxX,
                    y + 1.5,
                    z + part.maxZ);
            if (mask.intersectsWith(placed)) list.add(placed);
        }
    }

    private List<AxisAlignedBB> hodgepodge$parts(IBlockAccess world, int x, int y, int z) {
        boolean west = canConnectWallTo(world, x - 1, y, z), east = canConnectWallTo(world, x + 1, y, z);
        boolean north = canConnectWallTo(world, x, y, z - 1), south = canConnectWallTo(world, x, y, z + 1);
        boolean airAbove = world.isAirBlock(x, y + 1, z);
        List<AxisAlignedBB> parts = new ArrayList<>(5);
        if (airAbove && north && south && !west && !east) {
            parts.add(getBoundingBox(0.3125, 0, 0, 0.6875, 0.8125, 1));
        } else if (airAbove && west && east && !north && !south) {
            parts.add(getBoundingBox(0, 0, 0.3125, 1, 0.8125, 0.6875));
        } else {
            parts.add(getBoundingBox(0.25, 0, 0.25, 0.75, 1, 0.75));
            if (west) parts.add(getBoundingBox(0, 0, 0.3125, 0.25, 0.8125, 0.6875));
            if (east) parts.add(getBoundingBox(0.75, 0, 0.3125, 1, 0.8125, 0.6875));
            if (north) parts.add(getBoundingBox(0.3125, 0, 0, 0.6875, 0.8125, 0.25));
            if (south) parts.add(getBoundingBox(0.3125, 0, 0.75, 0.6875, 0.8125, 1));
        }
        return parts;
    }
}
