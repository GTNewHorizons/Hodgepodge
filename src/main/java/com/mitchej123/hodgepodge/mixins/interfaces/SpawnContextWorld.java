package com.mitchej123.hodgepodge.mixins.interfaces;

public interface SpawnContextWorld {

    int CONTEXT_NONE = 0;
    int CONTEXT_NATURAL = 1;
    int CONTEXT_CHUNK_GEN = 2;

    int hodgepodge$getSpawnContext();

    void hodgepodge$setSpawnContext(int context);
}
