package com.mitchej123.hodgepodge.mixins.early.minecraft.fastload;

import com.mitchej123.hodgepodge.server.NewIntCache;
import net.minecraft.world.gen.layer.IntCache;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(IntCache.class)
public class MixinIntCache {

    /**
     * @author ah-OOG-ah
     * @reason The old methods are non-threadsafe and barely safe at all - they recycle all allocated instances instead
     * of explicitly releasing them.
     */
    @Overwrite
    public static synchronized int[] getIntCache(int size) {
        return NewIntCache.getCache(size);
    }
}
