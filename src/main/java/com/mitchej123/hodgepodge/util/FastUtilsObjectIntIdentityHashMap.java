package com.mitchej123.hodgepodge.util;

import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.jetbrains.annotations.NotNull;

import it.unimi.dsi.fastutil.objects.Reference2IntMap;
import it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap;

public class FastUtilsObjectIntIdentityHashMap<K> extends IdentityHashMap<K, Integer> {

    private final Reference2IntMap<K> forwardMap;

    public FastUtilsObjectIntIdentityHashMap(int expectedMaxSize) {
        super(0); // Don't allocate in parent
        this.forwardMap = new Reference2IntOpenHashMap<>(expectedMaxSize);
        this.forwardMap.defaultReturnValue(-1);
    }

    @Override
    @Deprecated
    public Integer put(K key, Integer value) {
        return forwardMap.put(key, value);
    }

    public int put(K key, int value) {
        return forwardMap.put(key, value);
    }

    @Override
    public Integer get(Object key) {
        int value = forwardMap.getInt(key);
        return value == -1 ? null : value;
    }

    public int getInt(Objects key) {
        return forwardMap.getInt(key);
    }

    @Override
    public Integer getOrDefault(Object key, Integer defaultValue) {
        return containsKey(key) ? forwardMap.getInt(key) : defaultValue;
    }

    public int getIntOrDefault(Object key, int defaultValue) {
        return containsKey(key) ? forwardMap.getInt(key) : defaultValue;
    }

    @Override
    public boolean containsKey(Object key) {
        return forwardMap.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        if (value instanceof Integer intValue) {
            return forwardMap.containsValue(intValue.intValue());
        }
        return false;
    }

    @Override
    public Integer remove(Object key) {
        return forwardMap.removeInt(key);
    }

    @Override
    public void clear() {
        forwardMap.clear();
    }

    @Override
    public int size() {
        return forwardMap.size();
    }

    @Override
    public boolean isEmpty() {
        return forwardMap.isEmpty();
    }

    @Override
    public @NotNull Set<K> keySet() {
        return forwardMap.keySet();
    }

    @Override
    public @NotNull Collection<Integer> values() {
        return forwardMap.values();
    }

    @Override
    @Deprecated
    public @NotNull Set<Map.Entry<K, Integer>> entrySet() {
        return forwardMap.entrySet();
    }

}
