package com.mitchej123.hodgepodge.mixins.late.bibliocraft;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;

import com.mitchej123.hodgepodge.util.BlockRelativeSide;

import jds.bibliocraft.tileentities.TileEntityPrintPress;

@Mixin(TileEntityPrintPress.class)
public abstract class MixinBiblioCraftPrintPress extends TileEntity {

    /**
     * @author vladislemon
     * @reason better automation
     */
    @Overwrite
    public int[] getAccessibleSlotsFromSide(int side) {
        if (side == hodgepodge$getSide(BlockRelativeSide.BACK)) { // plate
            int[] sides = new int[1];
            sides[0] = 1;
            return sides;
        } else if (side == hodgepodge$getSide(BlockRelativeSide.RIGHT)) { // printed book
            int[] sides = new int[1];
            sides[0] = 3;
            return sides;
        } else {
            int[] sides = new int[2];
            sides[0] = 0;
            sides[1] = 2;
            return sides;
        }
    }

    /**
     * @author vladislemon
     * @reason better automation
     */
    @Overwrite
    public boolean canExtractItem(int slot, ItemStack itemstack, int side) {
        // plate
        if (slot == 1 && side == hodgepodge$getSide(BlockRelativeSide.BACK)) {
            return true;
        }
        // printed book
        if (slot == 3 && side == hodgepodge$getSide(BlockRelativeSide.RIGHT)) {
            return true;
        }
        return false;
    }

    @Unique
    private int hodgepodge$getSide(BlockRelativeSide blockSide) {
        return switch (blockSide) {
            case BOTTOM -> 0;
            case TOP -> 1;
            case LEFT -> switch (getBlockMetadata()) {
                    case 0 -> 2;
                    case 1 -> 5;
                    case 2 -> 3;
                    case 3 -> 4;
                    default -> -1;
                };
            case RIGHT -> switch (getBlockMetadata()) {
                    case 0 -> 3;
                    case 1 -> 4;
                    case 2 -> 2;
                    case 3 -> 5;
                    default -> -1;
                };
            case FRONT -> switch (getBlockMetadata()) {
                    case 0 -> 4;
                    case 1 -> 2;
                    case 2 -> 5;
                    case 3 -> 3;
                    default -> -1;
                };
            case BACK -> switch (getBlockMetadata()) {
                    case 0 -> 5;
                    case 1 -> 3;
                    case 2 -> 4;
                    case 3 -> 2;
                    default -> -1;
                };
        };
    }

}
