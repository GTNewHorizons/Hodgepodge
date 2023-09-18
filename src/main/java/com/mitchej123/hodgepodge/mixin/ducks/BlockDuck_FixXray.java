package com.mitchej123.hodgepodge.mixin.ducks;

import net.minecraft.world.World;

public interface BlockDuck_FixXray {

    boolean hodgepodge$shouldRayTraceStopOnBlock(World worldIn, int x, int y, int z);
}
