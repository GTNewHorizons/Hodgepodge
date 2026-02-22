package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRedstoneWire;
import net.minecraft.block.material.Material;
import net.minecraft.world.IBlockAccess;

import org.spongepowered.asm.mixin.Mixin;

@Mixin(BlockRedstoneWire.class)
public abstract class MixinBlockRedstoneWire_Hitbox extends Block {

    protected MixinBlockRedstoneWire_Hitbox(Material material) {
        super(material);
    }

    /**
     * @author John
     * @reason Make Block Bounds of Redstone more accurate depending on state.
     */
    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess blockAccess, int x, int y, int z) {
        boolean flag = BlockRedstoneWire.isPowerProviderOrWire(blockAccess, x - 1, y, z, 1)
                || !blockAccess.getBlock(x - 1, y, z).isBlockNormalCube()
                        && BlockRedstoneWire.isPowerProviderOrWire(blockAccess, x - 1, y - 1, z, -1);
        boolean flag1 = BlockRedstoneWire.isPowerProviderOrWire(blockAccess, x + 1, y, z, 3)
                || !blockAccess.getBlock(x + 1, y, z).isBlockNormalCube()
                        && BlockRedstoneWire.isPowerProviderOrWire(blockAccess, x + 1, y - 1, z, -1);
        boolean flag2 = BlockRedstoneWire.isPowerProviderOrWire(blockAccess, x, y, z - 1, 2)
                || !blockAccess.getBlock(x, y, z - 1).isBlockNormalCube()
                        && BlockRedstoneWire.isPowerProviderOrWire(blockAccess, x, y - 1, z - 1, -1);
        boolean flag3 = BlockRedstoneWire.isPowerProviderOrWire(blockAccess, x, y, z + 1, 0)
                || !blockAccess.getBlock(x, y, z + 1).isBlockNormalCube()
                        && BlockRedstoneWire.isPowerProviderOrWire(blockAccess, x, y - 1, z + 1, -1);

        if (!blockAccess.getBlock(x, y + 1, z).isBlockNormalCube()) {
            if (blockAccess.getBlock(x - 1, y, z).isBlockNormalCube()
                    && BlockRedstoneWire.isPowerProviderOrWire(blockAccess, x - 1, y + 1, z, -1)) {
                flag = true;
            }

            if (blockAccess.getBlock(x + 1, y, z).isBlockNormalCube()
                    && BlockRedstoneWire.isPowerProviderOrWire(blockAccess, x + 1, y + 1, z, -1)) {
                flag1 = true;
            }

            if (blockAccess.getBlock(x, y, z - 1).isBlockNormalCube()
                    && BlockRedstoneWire.isPowerProviderOrWire(blockAccess, x, y + 1, z - 1, -1)) {
                flag2 = true;
            }

            if (blockAccess.getBlock(x, y, z + 1).isBlockNormalCube()
                    && BlockRedstoneWire.isPowerProviderOrWire(blockAccess, x, y + 1, z + 1, -1)) {
                flag3 = true;
            }
        }

        float gap = 0.2F;
        float gap2 = gap;
        float gap3 = gap;
        float gap4 = gap2;
        if (flag) {
            gap = 0.0F;
        }
        if (flag1) {
            gap2 = 0.0F;
        }
        if (flag2) {
            gap3 = 0.0F;
        }
        if (flag3) {
            gap4 = 0.0F;
        }
        if ((flag || flag1) && !flag2 && !flag3) {
            gap = 0.0F;
            gap2 = 0.0F;
        }
        if ((flag2 || flag3) && !flag && !flag1) {
            gap3 = 0.0F;
            gap4 = 0.0F;
        }
        this.setBlockBounds(gap, 0.0F, gap3, 1 - gap2, 0.0625F, 1 - gap4);

    }
}
