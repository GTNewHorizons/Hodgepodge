package com.mitchej123.hodgepodge.mixins.hooks;

import it.unimi.dsi.fastutil.longs.LongArrayList;

public class DeferredChunkQueue {

    public final LongArrayList chunks = new LongArrayList();
    public boolean dirty;
}
