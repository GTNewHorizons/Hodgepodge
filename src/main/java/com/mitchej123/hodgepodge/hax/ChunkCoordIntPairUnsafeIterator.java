package com.mitchej123.hodgepodge.hax;

import java.util.Iterator;
import java.util.NoSuchElementException;

import net.minecraft.world.ChunkCoordIntPair;

import com.mitchej123.hodgepodge.mixins.interfaces.MutableChunkCoordIntPair;
import com.mitchej123.hodgepodge.util.ChunkPosUtil;

import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongSet;

public class ChunkCoordIntPairUnsafeIterator implements Iterator<ChunkCoordIntPair> {

    private final LongIterator longIterator;
    private final MutableChunkCoordIntPair reusablePair = (MutableChunkCoordIntPair) new ChunkCoordIntPair(0, 0);

    public ChunkCoordIntPairUnsafeIterator(LongSet longSet) {
        this.longIterator = longSet.iterator();
    }

    @Override
    public boolean hasNext() {
        return longIterator.hasNext();
    }

    @Override
    public ChunkCoordIntPair next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        final long pos = longIterator.nextLong();
        return (ChunkCoordIntPair) reusablePair.setChunkPos(ChunkPosUtil.getPackedX(pos), ChunkPosUtil.getPackedZ(pos));
    }
}
