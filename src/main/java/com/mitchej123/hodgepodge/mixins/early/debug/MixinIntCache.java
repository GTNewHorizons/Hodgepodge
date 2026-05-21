package com.mitchej123.hodgepodge.mixins.early.debug;

import java.util.List;

import net.minecraft.world.gen.layer.IntCache;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(IntCache.class)
public class MixinIntCache {

    @Shadow
    private static List freeSmallArrays;

    @Shadow
    private static List freeLargeArrays;

    @Shadow
    private static List inUseLargeArrays;

    @Shadow
    private static List inUseSmallArrays;

    /**
     * @author Alexdoru
     * @reason More readable info
     */
    @Overwrite
    public static synchronized String getCacheSizes() {
        return "IntCache: free (L: " + freeLargeArrays.size()
                + ", S: "
                + freeSmallArrays.size()
                + ") used (L: "
                + inUseLargeArrays.size()
                + ", S: "
                + inUseSmallArrays.size()
                + ")";
    }
}
