package com.mitchej123.hodgepodge.mixins.hooks;

import net.minecraft.world.World;

import com.mitchej123.supernova.world.ExtendedWorld;

public class SupernovaHelper {

    public static boolean hasChunkPendingLight(World world, int cx, int cz) {
        return ((ExtendedWorld) world).hasChunkPendingLight(cx, cz);
    }
}
