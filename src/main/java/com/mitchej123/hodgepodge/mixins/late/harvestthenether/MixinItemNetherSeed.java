package com.mitchej123.hodgepodge.mixins.late.harvestthenether;

import net.minecraft.block.Block;
import net.minecraft.item.ItemSeeds;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.IPlantable;

import org.spongepowered.asm.mixin.Mixin;

import com.mitchej123.hodgepodge.mixins.interfaces.INetherSeed;
import com.pam.harvestthenether.ItemNetherSeed;

@Mixin(ItemNetherSeed.class)
public abstract class MixinItemNetherSeed extends ItemSeeds implements IPlantable, INetherSeed {

    public MixinItemNetherSeed(Block p_i45352_1_, Block p_i45352_2_) {
        super(p_i45352_1_, p_i45352_2_);
    }

    @Override
    public Block hodgepodge$getPlant(IBlockAccess world, int x, int y, int z) {
        // The implementation in ItemSeeds is correct, use that
        return super.getPlant(world, x, y, z);
    }
}
