package com.mitchej123.hodgepodge.mixins.interfaces;

import net.minecraft.world.World;

public interface BlockExt_FixXray {

    boolean hodgepodge$shouldRayTraceStopOnBlock(World worldIn, int x, int y, int z);
}
