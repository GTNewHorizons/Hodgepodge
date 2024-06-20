package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.util.LongHashMap;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;

@Mixin(LongHashMap.class)
public class MixinLongHashMap {

    @Unique
    private transient Long2ObjectOpenHashMap<Object> fastMap;

    /**
     * @author NotAPenguin
     * @reason Switch out terrible custom hash map implementation with fastutil hashmap.
     */
    @Overwrite
    public int getNumHashElements() {
        return fastMap.size();
    }

    /**
     * @author NotAPenguin
     * @reason Switch out terrible custom hash map implementation with fastutil hashmap.
     */
    @Overwrite
    public Object getValueByKey(long key) {
        return fastMap.get(key);
    }

    /**
     * @author NotAPenguin
     * @reason Switch out terrible custom hash map implementation with fastutil hashmap.
     */
    @Overwrite
    public boolean containsItem(long key) {
        return fastMap.containsKey(key);
    }

    /**
     * @author NotAPenguin
     * @reason Switch out terrible custom hash map implementation with fastutil hashmap.
     */
    @Overwrite
    public void add(long key, Object object) {
        fastMap.put(key, object);
    }

    /**
     * @author NotAPenguin
     * @reason Switch out terrible custom hash map implementation with fastutil hashmap.
     */
    @Overwrite
    public Object remove(long key) {
        return fastMap.remove(key);
    }
}
