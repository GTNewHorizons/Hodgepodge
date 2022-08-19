package com.mitchej123.hodgepodge.mixins.harvestthenether;

import com.pam.harvestthenether.BlockPamFruit;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BlockPamFruit.class)
public class MixinBlockPamFruit extends Block {

    private MixinBlockPamFruit(Material materialIn) {
        super(materialIn);
    }
    
    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World worldIn, int x, int y, int z) {
        return null;
    }

}
