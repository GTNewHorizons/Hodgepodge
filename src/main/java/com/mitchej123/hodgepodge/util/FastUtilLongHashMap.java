package com.mitchej123.hodgepodge.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.locks.StampedLock;

import net.minecraft.util.LongHashMap;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;

public class FastUtilLongHashMap extends LongHashMap {

    private final transient StampedLock lock = new StampedLock();
    private final Long2ObjectMap<Object> map = new Long2ObjectOpenHashMap<>(32);

    @Override
    public int getNumHashElements() {
        long stamp = lock.tryOptimisticRead();
        int value = map.size();
        if (!lock.validate(stamp)) {
            stamp = lock.readLock();
            try {
                value = map.size();
            } finally {
                lock.unlockRead(stamp);
            }
        }
        return value;
    }

    @Override
    public Object getValueByKey(long key) {
        boolean crash = false;
        Object value = null;
        long stamp = lock.tryOptimisticRead();
        // Because reading data while writing can potentially cause exceptions (although I haven't
        // actually seen exceptions thrown in the real game), we catch any exceptions here and then
        // force a re-read under a lock if an exception occurs.
        try {
            value = map.get(key);
        } catch (Exception e) {
            crash = true;
        }
        if (crash || !lock.validate(stamp)) {
            stamp = lock.readLock();
            try {
                value = map.get(key);
            } finally {
                lock.unlockRead(stamp);
            }
        }
        return value;
    }

    @Override
    public boolean containsItem(long key) {
        boolean crash = false;
        boolean value = false;
        long stamp = lock.tryOptimisticRead();
        // Because reading data while writing can potentially cause exceptions (although I haven't
        // actually seen exceptions thrown in the real game), we catch any exceptions here and then
        // force a re-read under a lock if an exception occurs.
        try {
            value = map.containsKey(key);
        } catch (Exception e) {
            crash = true;
        }
        if (crash || !lock.validate(stamp)) {
            stamp = lock.readLock();
            try {
                value = map.containsKey(key);
            } finally {
                lock.unlockRead(stamp);
            }
        }
        return value;
    }

    @Override
    public void add(long key, Object value) {
        long stamp = lock.writeLock();
        try {
            map.put(key, value);
        } finally {
            lock.unlockWrite(stamp);
        }
    }

    @Override
    public Object remove(long key) {
        long stamp = lock.writeLock();
        try {
            return map.remove(key);
        } finally {
            lock.unlockWrite(stamp);
        }
    }

    public Iterator<Object> valuesIterator() {
        long stamp = lock.readLock();
        try {
            return new ArrayList<>(map.values()).iterator();
        } finally {
            lock.unlockRead(stamp);
        }
    }
}
