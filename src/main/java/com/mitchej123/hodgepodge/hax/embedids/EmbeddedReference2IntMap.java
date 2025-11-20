package com.mitchej123.hodgepodge.hax.embedids;

import net.minecraft.util.ObjectIntIdentityMap;

import com.mitchej123.hodgepodge.util.EmbeddedObjectIntMap;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

/// This has less allocation spam compared to the original, and delegates the actual storage to a direct "map". Writes
/// still pass through to an actual map, but reads are simply a field get on the global Block/Item object itself.
///
/// The downside is that *only one* of these maps can exist at any given time, for any given type. If you make multiple
/// they WILL explode, since they all effectively share storage.
public class EmbeddedReference2IntMap extends ObjectIntIdentityMap {

    private final EmbeddedObjectIntMap<Object> objectMap;
    private final ObjectArrayList<Object> objectList;

    public EmbeddedReference2IntMap() {
        objectMap = new EmbeddedObjectIntMap<>(512);
        field_148749_a = objectMap;

        objectList = new ObjectArrayList<>(512);
        field_148748_b = objectList;
    }

    @Override
    public void func_148746_a(Object key, int val) {
        objectMap.put(key, val);
        objectList.ensureCapacity(val + 1);
        while (objectList.size() <= val) {
            objectList.add(null);
        }
        objectList.set(val, key);
    }

    @Override
    public int func_148747_b(Object key) {
        return objectMap.getInt(key);
    }

    @Override
    public Object func_148745_a(int val) {
        return val >= 0 && val < this.objectList.size() ? this.objectList.get(val) : null;
    }

    @Override
    public boolean func_148744_b(int val) {
        return hodgepodge$getByValue(val) != null;
    }

    public Object hodgepodge$getByValue(int value) {
        return value >= 0 && value < this.objectList.size() ? this.objectList.get(value) : null;
    }

    public void setType(Class<?> type) {
        objectMap.setType(type);
    }
}
