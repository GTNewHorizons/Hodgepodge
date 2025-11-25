package com.mitchej123.hodgepodge.mixins.interfaces;

public interface HasID {

    default int hodgepodge$getID() {
        throw new UnsupportedOperationException("Needs to be overridden via Mixin!");
    }

    default void hodgepodge$setID(int id) {
        throw new UnsupportedOperationException("Needs to be overridden via Mixin!");
    }
}
