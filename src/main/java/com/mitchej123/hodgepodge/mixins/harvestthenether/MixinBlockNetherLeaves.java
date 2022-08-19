package com.mitchej123.hodgepodge.mixins.harvestthenether;

import com.mitchej123.hodgepodge.client.IGraphicsLevelSetter;
import com.pam.harvestthenether.BlockNetherLeaves;
import net.minecraft.block.BlockLeavesBase;
import net.minecraft.block.material.Material;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BlockNetherLeaves.class)
public class MixinBlockNetherLeaves extends BlockLeavesBase implements IGraphicsLevelSetter {

    private MixinBlockNetherLeaves(Material p_i45433_1_, boolean p_i45433_2_) {
        super(p_i45433_1_, p_i45433_2_);
    }

    @Override
    public void setGraphicsLevel(boolean level) {
        this.field_150121_P = level;
    }

}
