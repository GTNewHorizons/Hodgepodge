package com.mitchej123.hodgepodge.util;

import it.unimi.dsi.fastutil.longs.LongComparator;
import it.unimi.dsi.fastutil.longs.LongPredicate;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.ChunkCoordIntPair;

import java.util.Comparator;

public class ChunkPosUtil {

    public static final long INT_MASK = (1L << Integer.SIZE) - 1;
    public static final long INVALID_COORD = Long.MAX_VALUE;

    public static int getPackedX(long pos) {
        return (int) (pos & INT_MASK);
    }

    public static int getPackedZ(long pos) {
        return (int) (pos >>> 32 & INT_MASK);
    }

    public static long toLong(int x, int z) {
        return (long) x & 4294967295L | ((long) z & 4294967295L) << 32;
    }

    public static long toLong(Object o) {

        if (o instanceof ChunkCoordIntPair c)
            return toLong(c.chunkXPos, c.chunkZPos);

        return INVALID_COORD;
    }

    public static class ObjComparator implements Comparator<ChunkCoordIntPair> {

        private int cx;
        private int cz;

        public ObjComparator setPos(int cx, int cz) {
            this.cx = cx;
            this.cz = cz;
            return this;
        }

        /**
         * Returns a negative value if the first position is closer than the second, and vice versa. Returns zero if the
         * positions are identical or equally far from the set position.
         */
        @Override
        public int compare(ChunkCoordIntPair c1, ChunkCoordIntPair c2) {

            if (c1 == c2)
                return 0;

            final int dx1 = c1.chunkXPos - cx;
            final int dz1 = c1.chunkZPos - cz;
            final int dist1Sq = dx1 * dx1 + dz1 * dz1;

            final int dx2 = c2.chunkXPos - cx;
            final int dz2 = c2.chunkZPos - cz;
            final int dist2Sq = dx2 * dx2 + dz2 * dz2;

            return Integer.compare(dist1Sq, dist2Sq);
        }
    }

    public static class FastComparator implements LongComparator {

        private int cx;
        private int cz;

        public FastComparator setPos(int cx, int cz) {
            this.cx = cx;
            this.cz = cz;
            return this;
        }

        public FastComparator setPos(EntityPlayerMP player) {
            this.cx = (int) player.posX >> 4;
            this.cz = (int) player.posZ >> 4;
            return this;
        }

        public boolean withinRadius(long key, int renderDistance) {
            if (Math.abs(getPackedX(key) - this.cx) > renderDistance)
                return false;

            if (Math.abs(getPackedZ(key) - this.cz) > renderDistance)
                return false;

            return true;
        }

        /**
         * Returns a negative value if the first position is closer than the second, and vice versa. Returns zero if the
         * positions are identical or equally far from the set position.
         */
        @Override
        public int compare(long c1, long c2) {

            if (c1 == c2)
                return 0;

            final int dx1 = getPackedX(c1) - cx;
            final int dz1 = getPackedZ(c1) - cz;
            final int dist1Sq = dx1 * dx1 + dz1 * dz1;

            final int dx2 = getPackedX(c2) - cx;
            final int dz2 = getPackedZ(c2) - cz;
            final int dist2Sq = dx2 * dx2 + dz2 * dz2;

            return Integer.compare(dist1Sq, dist2Sq);
        }
    }
}
