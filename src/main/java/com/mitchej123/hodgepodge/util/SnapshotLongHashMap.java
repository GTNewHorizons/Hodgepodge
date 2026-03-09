package com.mitchej123.hodgepodge.util;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;

public class SnapshotLongHashMap extends FastUtilLongHashMap {

    private volatile Long2ObjectOpenHashMap<Object> snapshot = new Long2ObjectOpenHashMap<>();
    private boolean dirty = false;

    @Override
    public Object getValueByKey(long key) {
        if (Thread.currentThread() == ownerThread) {
            return map.get(key);
        }
        return snapshot.get(key);
    }

    @Override
    public boolean containsItem(long key) {
        if (Thread.currentThread() == ownerThread) {
            return map.containsKey(key);
        }
        return snapshot.containsKey(key);
    }

    @Override
    public int getNumHashElements() {
        if (Thread.currentThread() == ownerThread) {
            return map.size();
        }
        return snapshot.size();
    }

    @Override
    public void add(long key, Object value) {
        checkOwner();
        map.put(key, value);
        dirty = true;
    }

    @Override
    public Object remove(long key) {
        checkOwner();
        dirty = true;
        return map.remove(key);
    }

    @SuppressWarnings("unchecked")
    public void publishSnapshot() {
        if (dirty) {
            snapshot = map.clone();
            dirty = false;
        }
    }
}
