package com.mitchej123.hodgepodge.util;

public class ChunkPosUtil {

    public static long INT_MASK = (1L << Integer.SIZE) - 1;

    public static int getPackedX(long pos) {
        return (int) (pos & INT_MASK);
    }

    public static int getPackedZ(long pos) {
        return (int) (pos >>> 32 & INT_MASK);
    }

    public static long toLong(int x, int z) {
        return (long) x & 4294967295L | ((long) z & 4294967295L) << 32;
    }

}
