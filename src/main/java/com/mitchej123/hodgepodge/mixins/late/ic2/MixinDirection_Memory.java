package com.mitchej123.hodgepodge.mixins.late.ic2;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;

import ic2.api.Direction;

@Mixin(value = Direction.class, remap = false)
public abstract class MixinDirection_Memory {

    /**
     * @author Alexdoru
     * @reason Reduce allocation spam
     */
    @Overwrite
    public TileEntity applyTo(World world, int x, int y, int z) {
        if (world == null) return null;
        return switch ((Direction) (Object) this) {
            case XN -> hodgepodge$getTE(world, x - 1, y, z);
            case XP -> hodgepodge$getTE(world, x + 1, y, z);
            case YN -> hodgepodge$getTE(world, x, y - 1, z);
            case YP -> hodgepodge$getTE(world, x, y + 1, z);
            case ZN -> hodgepodge$getTE(world, x, y, z - 1);
            case ZP -> hodgepodge$getTE(world, x, y, z + 1);
        };
    }

    @Unique
    private TileEntity hodgepodge$getTE(World world, int x, int y, int z) {
        if (world.blockExists(x, y, z)) {
            return world.getTileEntity(x, y, z);
        } else {
            return null;
        }
    }

}
