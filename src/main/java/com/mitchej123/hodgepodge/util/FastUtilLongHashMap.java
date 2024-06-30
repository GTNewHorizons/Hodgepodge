package com.mitchej123.hodgepodge.util;

import net.minecraft.util.LongHashMap;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;

public class FastUtilLongHashMap extends LongHashMap {

    private final Long2ObjectMap<Object> map;

    public FastUtilLongHashMap() {
        map = new Long2ObjectOpenHashMap<>();
    }

    @Override
    public int getNumHashElements() {
        return map.size();
    }

    @Override
    public Object getValueByKey(long key) {
        return map.get(key);
    }

    @Override
    public boolean containsItem(long key) {
        return map.containsKey(key);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void add(long key, Object value) {
        map.put(key, value);
    }

    @Override
    public Object remove(long key) {
        return map.remove(key);
    }
}
