package com.mitchej123.hodgepodge.client;

public enum F1State {

    SHOW_ALL,
    SHOW_HAND,
    HIDE_ALL;

    public static F1State state = F1State.SHOW_ALL;

    public void cycle() {
        state = switch (this) {
            case SHOW_ALL -> SHOW_HAND;
            case SHOW_HAND -> HIDE_ALL;
            case HIDE_ALL -> SHOW_ALL;
        };
    }
}
