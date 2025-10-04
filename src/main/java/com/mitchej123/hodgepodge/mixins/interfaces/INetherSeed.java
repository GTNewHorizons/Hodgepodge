package com.mitchej123.hodgepodge.mixins.interfaces;

import net.minecraft.block.Block;
import net.minecraft.world.IBlockAccess;

public interface INetherSeed {

    Block hodgepodge$getPlant(IBlockAccess world, int x, int y, int z);
}
