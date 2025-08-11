package com.mitchej123.hodgepodge.util;

import java.util.Random;

/**
 * A basic LCG, based on Knuth's work and using the same constants as {@link Random}. As such, it produces the same
 * output, just MUCH faster since it's not reliant on atomic ops. However, each instance can only be used by one thread
 * at a time.
 */
public class StdLCG extends Random {

    private static final long serialVersionUID = 3029703444052229824L;
    private static final long mask = -1L >>> 16;
    private static final long multiplier = 0x5DEECE66DL;
    private static final long increment = 11;

    // This isn't technically identical to super, but stdlib uses nanotime anyways
    // It doesn't have to be *that* identical
    private long seed = new Random().nextLong() & mask;

    public StdLCG() {}

    /**
     * Don't ask me why stdlib does this, because I don't know.
     */
    public StdLCG(long seed) {
        this.seed = (seed ^ 25214903917L) & mask;
    }

    @Override
    public void setSeed(long seed) {
        this.seed = seed;
    }

    @Override
    protected int next(int bits) {
        seed = (seed * multiplier + increment) & mask;
        return (int) (seed >>> 48 - bits);
    }
}
