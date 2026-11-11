package com.mitchej123.hodgepodge.mixins.hooks;

public class ChunkGenBudget {

    private long terrainGenEmaUs = 150_000;
    private long populateEmaUs = 150_000;
    // Smoothing factor: 0.15 = new samples move the estimate ~15% per update
    private static final double ALPHA = 0.15;

    private long debtNanos = 0;

    public void recordTerrainGen(long durationNanos) {
        final long durationUs = durationNanos / 1000;
        terrainGenEmaUs = (long) (ALPHA * durationUs + (1 - ALPHA) * terrainGenEmaUs);
    }

    public void recordPopulate(long durationNanos) {
        final long durationUs = durationNanos / 1000;
        populateEmaUs = (long) (ALPHA * durationUs + (1 - ALPHA) * populateEmaUs);
    }

    public boolean canAffordTerrainGen(long budgetRemainingNanos) {
        return terrainGenEmaUs * 1000 <= budgetRemainingNanos;
    }

    public boolean canAffordPopulate(long budgetRemainingNanos) {
        return populateEmaUs * 1000 <= budgetRemainingNanos;
    }

    public double getTerrainGenEmaMs() {
        return terrainGenEmaUs / 1000.0;
    }

    public double getPopulateEmaMs() {
        return populateEmaUs / 1000.0;
    }

    public void payDown(long amount) {
        debtNanos -= amount;
        if (debtNanos < 0) debtNanos = 0;
    }

    public void recordOverrun(long budgetNanos, long actualNanos) {
        if (actualNanos > budgetNanos) {
            debtNanos = Math.min(actualNanos - budgetNanos, budgetNanos * 40);
        }
    }

    public boolean hasDebt() {
        return debtNanos > 0;
    }

    public double getDebtMs() {
        return debtNanos / 1_000_000.0;
    }
}
