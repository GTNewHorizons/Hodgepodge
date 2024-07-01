package com.mitchej123.hodgepodge.mixins.interfaces;

public interface FastWorldServer {

    /**
     * If true, the server has hit the maximum number of chunks to load that tick and new generation should be avoided,
     * where possible.
     */
    boolean isThrottlingGen();

    /**
     * Returns the number of chunks left before the server starts throttling generation
     */
    int remainingGenBudget();

    /**
     * Reduce the generation budget this tick by amount.
     */
    void spendGenBudget(int amount);
}
