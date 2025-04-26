package com.mitchej123.hodgepodge.util;

import net.minecraft.util.ObjectIntIdentityMap;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

// Used via ASM
@SuppressWarnings("unused")
public class ProperObjectIntIdentityMap extends ObjectIntIdentityMap {
    // Avoid boxing/unboxing ints!

    protected final FastUtilsObjectIntIdentityHashMap<Object> objectMap;
    protected final ObjectArrayList<Object> objectList;

    public ProperObjectIntIdentityMap() {
        super();
        objectMap = new FastUtilsObjectIntIdentityHashMap<>(512);
        objectList = new ObjectArrayList<>(512);
        super.field_148749_a = objectMap;
        super.field_148748_b = objectList;
    }

    @Override
    public void func_148746_a(Object key, int val) {
        put(key, val);
    }

    @Override
    public int func_148747_b(Object key) {
        return get(key);
    }

    @Override
    public Object func_148745_a(int val) {
        return getByValue(val);
    }

    @Override
    public boolean func_148744_b(int val) {
        return contains(val);
    }

    public void put(Object key, int value) {
        objectMap.put(key, value);
        objectList.ensureCapacity(value + 1);
        while (objectList.size() <= value) {
            objectList.add(null);
        }
        objectList.set(value, key);
    }

    private void ensureSize(int value) {

    }

    public int get(Object key) {
        return objectMap.getIntOrDefault(key, -1);
    }

    public Object getByValue(int value) {
        return value >= 0 && value < this.objectList.size() ? this.objectList.get(value) : null;
    }

    public boolean contains(int value) {
        return getByValue(value) != null;
    }

}
