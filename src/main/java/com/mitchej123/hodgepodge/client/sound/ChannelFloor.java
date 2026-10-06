package com.mitchej123.hodgepodge.client.sound;

/** Keeps a channel count another mod asked for from being lowered by ours. */
public final class ChannelFloor {

    public static final ChannelFloor NORMAL = new ChannelFloor(28);
    public static final ChannelFloor STREAMING = new ChannelFloor(4);

    private final int paulscodeDefault;
    private int requested;
    private int applied = -1;

    ChannelFloor(int paulscodeDefault) {
        this.paulscodeDefault = paulscodeDefault;
    }

    /** @param current what SoundSystemConfig holds now; differs from our last write if someone else set it */
    public int resolve(int ours, int current) {
        if (current != applied) requested = applied < 0 && current == paulscodeDefault ? 0 : current;
        return applied = Math.max(ours, requested);
    }
}
