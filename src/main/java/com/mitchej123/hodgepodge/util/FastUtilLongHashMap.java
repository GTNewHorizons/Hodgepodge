package com.mitchej123.hodgepodge.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import net.minecraft.util.LongHashMap;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;

public class FastUtilLongHashMap extends LongHashMap {

    private final Long2ObjectMap<Object> map = new Long2ObjectOpenHashMap<>();
    private final ReadWriteLock rwLock = new ReentrantReadWriteLock();
    private final Lock readLock = rwLock.readLock();
    private final Lock writeLock = rwLock.writeLock();

    @Override
    public int getNumHashElements() {
        readLock.lock();
        try {
            return map.size();
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public Object getValueByKey(long key) {
        readLock.lock();
        try {
            return map.get(key);
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public boolean containsItem(long key) {
        readLock.lock();
        try {
            return map.containsKey(key);
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public void add(long key, Object value) {
        writeLock.lock();
        try {
            map.put(key, value);
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public Object remove(long key) {
        writeLock.lock();
        try {
            return map.remove(key);
        } finally {
            writeLock.unlock();
        }
    }

    public Iterator<Object> valuesIterator() {
        readLock.lock();
        try {
            return new ArrayList<>(map.values()).iterator();
        } finally {
            readLock.unlock();
        }
    }
}
