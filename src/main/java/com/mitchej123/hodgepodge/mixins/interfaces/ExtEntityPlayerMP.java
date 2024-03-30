package com.mitchej123.hodgepodge.mixins.interfaces;

import it.unimi.dsi.fastutil.longs.LongOpenHashSet;

public interface ExtEntityPlayerMP {

    void setThrottled(boolean val);

    void setWasThrottled(boolean val);

    boolean isThrottled();

    boolean wasThrottled();

    LongOpenHashSet chunksToLoad();

    LongOpenHashSet loadedChunks();
}
