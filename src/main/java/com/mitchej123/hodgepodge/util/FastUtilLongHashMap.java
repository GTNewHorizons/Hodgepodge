package com.mitchej123.hodgepodge.util;

import java.util.ArrayList;
import java.util.Iterator;

import net.minecraft.util.LongHashMap;

import org.jctools.maps.NonBlockingHashMapLong;

public class FastUtilLongHashMap extends LongHashMap {

    private final NonBlockingHashMapLong<Object> map = new NonBlockingHashMapLong<>();

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
    public void add(long key, Object value) {
        map.put(key, value);
    }

    @Override
    public Object remove(long key) {
        return map.remove(key);
    }

    public Iterator<Object> valuesIterator() {
        return new ArrayList<>(map.values()).iterator();
    }
}
