package com.mitchej123.hodgepodge.hax;

import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.NotNull;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

public class FastIntCache {

    private static final int SMALLEST = 256;
    private static final int MIN_LEVEL = 32 - Integer.numberOfLeadingZeros(SMALLEST - 1);

    private static final Map<Integer, List<int[]>> cachedObjects = new Int2ObjectOpenHashMap<>();

    public static synchronized int[] getCache(int size) {
        // Get the smallest power of two larger than or equal to the number
        final int level = (size <= SMALLEST) ? MIN_LEVEL : 32 - Integer.numberOfLeadingZeros(size - 1);

        final List<int[]> caches = cachedObjects.computeIfAbsent(level, i -> new ObjectArrayList<>());

        if (caches.isEmpty()) return new int[2 << (level - 1)];
        return caches.remove(caches.size() - 1);
    }

    public static synchronized void releaseCache(int @NotNull [] cache) {
        final int level = (cache.length <= SMALLEST) ? MIN_LEVEL : 32 - Integer.numberOfLeadingZeros(cache.length - 1);
        cachedObjects.computeIfAbsent(level, i -> new ObjectArrayList<>()).add(cache);
    }
}
