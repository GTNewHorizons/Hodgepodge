package com.mitchej123.hodgepodge.util;

import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;

import org.jetbrains.annotations.NotNull;

import com.mitchej123.hodgepodge.mixins.interfaces.HasID;

import it.unimi.dsi.fastutil.objects.Reference2IntMap;
import it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap;

public class EmbeddedObjectIntMap<K> extends IdentityHashMap<K, Integer> {

    private static final long serialVersionUID = 7695726922765252503L;
    private final Reference2IntMap<K> forwardMap;
    private Class<?> type = null;
    private boolean useEmbed = true;

    public EmbeddedObjectIntMap(int expectedMaxSize) {
        super(0); // Don't allocate in parent
        this.forwardMap = new Reference2IntOpenHashMap<>(expectedMaxSize);
        this.forwardMap.defaultReturnValue(-1);
    }

    public void setEmbed(boolean useEmbed) {
        this.useEmbed = useEmbed;
    }

    public void setType(Class<?> type) {
        this.type = type;
    }

    @Override
    @Deprecated
    public Integer put(K key, Integer value) {
        final int ret = put(key, value.intValue());
        return ret == -1 ? null : ret;
    }

    public int put(K key, int value) {
        final Class<?> keyClass = key.getClass();
        if (type != null && !type.isAssignableFrom(keyClass))
            throw new ClassCastException("Expected a " + type.getName() + " but got a " + keyClass.getName());

        if (key instanceof HasID idHaver) idHaver.hodgepodge$setID(value);
        return forwardMap.put(key, value);
    }

    @Override
    public Integer get(Object key) {
        int value = getInt(key);

        return value == -1 ? null : value;
    }

    public int getInt(Object key) {
        if (key == null) return -1;

        final Class<?> keyClass = key.getClass();
        if (type != null && !type.isAssignableFrom(keyClass)) return -1;
        if (useEmbed && key instanceof HasID idHaver) return idHaver.hodgepodge$getID();

        return forwardMap.getInt(key);
    }

    @Override
    public Integer getOrDefault(Object key, Integer defaultValue) {
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
        if (key instanceof HasID idHaver) idHaver.hodgepodge$setID(-1);

        return forwardMap.removeInt(key);
    }

    @Override
    public void clear() {
        forwardMap.forEach((k, v) -> { if (k instanceof HasID idHaver) idHaver.hodgepodge$setID(-1); });

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
