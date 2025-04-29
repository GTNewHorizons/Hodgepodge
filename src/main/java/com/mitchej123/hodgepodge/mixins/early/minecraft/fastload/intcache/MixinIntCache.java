package com.mitchej123.hodgepodge.mixins.early.minecraft.fastload.intcache;

import net.minecraft.world.gen.layer.IntCache;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import com.mitchej123.hodgepodge.hax.FastIntCache;

@Mixin(IntCache.class)
public class MixinIntCache {

    /**
     * @author ah-OOG-ah
     * @reason The old methods are non-threadsafe and barely safe at all - they recycle all allocated instances instead
     *         of explicitly releasing them.
     */
    @Overwrite
    public static synchronized int[] getIntCache(int size) {
        return FastIntCache.getCache(size);
    }

    /**
     * Note: this should still be removed where possible! I don't know for sure whether the JIT can optimize out blank
     * synchronize methods, but I wouldn't trust it.
     *
     * @author ah-OOG-ah
     * @reason Unnecessary, since explicit freeing is used now
     */
    @Overwrite
    @Deprecated
    public static synchronized void resetIntCache() {

    }
}
