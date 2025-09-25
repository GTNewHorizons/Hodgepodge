package com.mitchej123.hodgepodge;

import java.util.HashSet;

import cpw.mods.fml.common.FMLLog;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

public class LoggingNBTTagMap<K, V> extends Object2ObjectOpenHashMap<K, V> {

    Thread thread;

    private static final HashSet<Object> warned = new HashSet<>();

    public LoggingNBTTagMap() {
        thread = Thread.currentThread();
    }

    private void check(Object k) {
        if (thread != Thread.currentThread()) {
            synchronized (warned) {
                if (!warned.contains(k)) {
                    warned.add(k);
                    FMLLog.warning("Cross thread access in NBTTagCompound!");
                    Thread.dumpStack();
                }
            }

        }
    }

    @Override
    public V put(final K k, final V v) {
        check(k);
        return super.put(k, v);
    }

    @Override
    public V remove(Object k) {
        check(k);
        return super.remove(k);
    }
}
