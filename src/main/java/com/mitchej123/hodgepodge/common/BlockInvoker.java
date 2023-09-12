package com.mitchej123.hodgepodge.common;

import net.minecraft.world.World;

public interface BlockInvoker {

    boolean hodgepodge$shouldRayTraceStopOnBlock(World worldIn, int x, int y, int z);
}
