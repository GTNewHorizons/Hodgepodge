package com.mitchej123.hodgepodge.hax;

import java.util.Iterator;
import java.util.NoSuchElementException;

import net.minecraft.world.ChunkCoordIntPair;

import com.mitchej123.hodgepodge.util.ChunkPosUtil;

import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongSet;

public class ChunkCoordIntPairIterator implements Iterator<ChunkCoordIntPair> {

    private final LongIterator longIterator;

    public ChunkCoordIntPairIterator(LongSet longSet) {
        this.longIterator = longSet.iterator();
    }

    @Override
    public boolean hasNext() {
        return longIterator.hasNext();
    }

    @Override
    public ChunkCoordIntPair next() {
        if (!longIterator.hasNext()) {
            throw new NoSuchElementException();
        }
        final long pos = longIterator.nextLong();
        return new ChunkCoordIntPair(ChunkPosUtil.getPackedX(pos), ChunkPosUtil.getPackedZ(pos));
    }
}
