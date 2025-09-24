package com.mitchej123.hodgepodge;

import cpw.mods.fml.common.FMLLog;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

public class LoggingNBTTagMap<K, V> extends Object2ObjectOpenHashMap<K, V> {

    Thread thread;

    public LoggingNBTTagMap() {
        thread = Thread.currentThread();
    }

    @Override
    public V put(final K k, final V v) {
        if (thread != Thread.currentThread()) {
            FMLLog.warning("Cross thread access in NBTTagCompound!");
            Thread.dumpStack();
        }
        return super.put(k, v);
    }

    @Override
    public V remove(Object k) {
        if (thread != Thread.currentThread()) {
            FMLLog.warning("Cross thread access in NBTTagCompound!");
            Thread.dumpStack();
        }
        return super.remove(k);
    }

    @Override
    public void clear() {
        if (thread != Thread.currentThread()) {
            FMLLog.warning("Cross thread access in NBTTagCompound!");
            Thread.dumpStack();
        }
        super.clear();
    }
}
