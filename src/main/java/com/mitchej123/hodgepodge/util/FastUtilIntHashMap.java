package com.mitchej123.hodgepodge.util;

import net.minecraft.util.IntHashMap;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

@SuppressWarnings("unused")
public class FastUtilIntHashMap extends IntHashMap {

    private final Int2ObjectOpenHashMap<Object> map = new Int2ObjectOpenHashMap<>();

    @Override
    public Object lookup(int key) {
        return map.get(key);
    }

    @Override
    public boolean containsItem(int key) {
        return map.containsKey(key);
    }

    @Override
    public void addKey(int key, Object value) {
        map.put(key, value);
    }

    @Override
    public void clearMap() {
        map.clear();
    }

    @Override
    public Object removeObject(int key) {
        return map.remove(key);
    }
}
