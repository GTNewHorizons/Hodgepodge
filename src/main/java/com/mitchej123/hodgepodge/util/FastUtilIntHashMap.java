package com.mitchej123.hodgepodge.util;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import net.minecraft.util.IntHashMap;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

public class FastUtilIntHashMap extends IntHashMap {

    private final Int2ObjectMap<Object> map = new Int2ObjectOpenHashMap<>();
    private final ReadWriteLock rwLock = new ReentrantReadWriteLock();
    private final Lock readLock = rwLock.readLock();
    private final Lock writeLock = rwLock.writeLock();

    @Override
    public Object lookup(int key) {
        readLock.lock();
        try {
            return map.get(key);
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public boolean containsItem(int key) {
        readLock.lock();
        try {
            return map.containsKey(key);
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public void addKey(int key, Object value) {
        writeLock.lock();
        try {
            map.put(key, value);
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public void clearMap() {
        writeLock.lock();
        try {
            map.clear();
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public Object removeObject(int key) {
        writeLock.lock();
        try {
            return map.remove(key);
        } finally {
            writeLock.unlock();
        }
    }
}
