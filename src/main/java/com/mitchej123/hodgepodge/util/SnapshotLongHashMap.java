package com.mitchej123.hodgepodge.util;

import org.jetbrains.annotations.ApiStatus;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;

public class SnapshotLongHashMap extends FastUtilLongHashMap {

    private volatile Long2ObjectOpenHashMap<Object> snapshot = new Long2ObjectOpenHashMap<>();
    private boolean dirty = false;

    @ApiStatus.Internal
    @Override
    public Object getValueByKey(long key) {
        if (Thread.currentThread() == ownerThread) {
            return map.get(key);
        }
        return snapshot.get(key);
    }

    @ApiStatus.Internal
    @Override
    public boolean containsItem(long key) {
        if (Thread.currentThread() == ownerThread) {
            return map.containsKey(key);
        }
        return snapshot.containsKey(key);
    }

    @ApiStatus.Internal
    @Override
    public int getNumHashElements() {
        if (Thread.currentThread() == ownerThread) {
            return map.size();
        }
        return snapshot.size();
    }

    @ApiStatus.Internal
    @Override
    public void add(long key, Object value) {
        checkOwner();
        map.put(key, value);
        dirty = true;
    }

    @ApiStatus.Internal
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
