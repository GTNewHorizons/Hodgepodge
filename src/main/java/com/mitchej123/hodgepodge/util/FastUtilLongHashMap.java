package com.mitchej123.hodgepodge.util;

import java.util.Iterator;

import net.minecraft.util.LongHashMap;

import org.jetbrains.annotations.ApiStatus;

import com.mitchej123.hodgepodge.core.shared.HodgepodgeProperties;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;

public class FastUtilLongHashMap extends LongHashMap {

    protected final Long2ObjectOpenHashMap<Object> map = new Long2ObjectOpenHashMap<>();
    protected final Thread ownerThread = Thread.currentThread();

    protected void checkOwner() {
        if (HodgepodgeProperties.ASSERT_MAP_THREAD_OWNER && Thread.currentThread() != ownerThread) {
            // separate method to reduce bytecode count and help JIT inline the method
            this.fail();
        }
    }

    private void fail() {
        throw new IllegalStateException(
                "FastUtilLongHashMap accessed from " + Thread.currentThread().getName()
                        + ", owner is "
                        + ownerThread.getName());
    }

    /** @deprecated Reobfuscated in prod. */
    @ApiStatus.Internal
    @Override
    public int getNumHashElements() {
        checkOwner();
        return map.size();
    }

    /** @deprecated Reobfuscated in prod. */
    @ApiStatus.Internal
    @Override
    public Object getValueByKey(long key) {
        checkOwner();
        return map.get(key);
    }

    /** @deprecated Reobfuscated in prod. */
    @ApiStatus.Internal
    @Override
    public boolean containsItem(long key) {
        checkOwner();
        return map.containsKey(key);
    }

    /** @deprecated Reobfuscated in prod. */
    @ApiStatus.Internal
    @Override
    public void add(long key, Object value) {
        checkOwner();
        map.put(key, value);
    }

    /** @deprecated Reobfuscated in prod. */
    @ApiStatus.Internal
    @Override
    public Object remove(long key) {
        checkOwner();
        return map.remove(key);
    }

    public Iterator<Object> valuesIterator() {
        checkOwner();
        return map.values().iterator();
    }
}
