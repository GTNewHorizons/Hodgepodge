package com.mitchej123.hodgepodge.mixins.hooks;

public class ChunkGenStats {

    private final ChunkGenBudget budget;

    private int snapGenQueue, snapPopQueue, snapPipelineBacklog;
    private int snapTerrainGenCount, snapPopCount;
    private int snapNeighborFills, snapStuckCount, snapResendRetries;
    private double snapBudgetUsedMs, snapBudgetAvailMs;
    private int snapBlockedLoads;
    private boolean snapInDebt;

    private int neighborFillCount;
    private int stuckCount;
    private int resendRetryCount;

    ChunkGenStats(int dimId, ChunkGenBudget budget) {
        this.budget = budget;
    }

    public void addNeighborFills(int count) {
        neighborFillCount += count;
    }

    public void incrementStuck() {
        stuckCount++;
    }

    public void incrementResendRetry() {
        resendRetryCount++;
    }

    public void recordTick(int genQueue, int popQueue, int terrainStatusCount, int blockedLoads, int terrainGenCount,
            long terrainNanos, int popCount, long popNanos, long actualNanos, long tickBudgetNanos, boolean inDebt) {
        final double budgetUsedMs = actualNanos / 1_000_000.0;
        final double budgetAvailMs = tickBudgetNanos / 1_000_000.0;
        final int fills = resetNeighborFillCount();
        final int stuck = resetStuckCount();
        final int resendRetries = resetResendRetryCount();

        this.snapGenQueue = genQueue;
        this.snapPopQueue = popQueue;
        this.snapPipelineBacklog = terrainStatusCount;
        this.snapTerrainGenCount = terrainGenCount;
        this.snapPopCount = popCount;
        this.snapNeighborFills = fills;
        this.snapStuckCount = stuck;
        this.snapResendRetries = resendRetries;
        this.snapBudgetUsedMs = budgetUsedMs;
        this.snapBudgetAvailMs = budgetAvailMs;
        this.snapBlockedLoads = blockedLoads;
        this.snapInDebt = inDebt;
    }

    private int resetNeighborFillCount() {
        final int c = neighborFillCount;
        neighborFillCount = 0;
        return c;
    }

    private int resetStuckCount() {
        final int c = stuckCount;
        stuckCount = 0;
        return c;
    }

    private int resetResendRetryCount() {
        final int c = resendRetryCount;
        resendRetryCount = 0;
        return c;
    }

    public int getSnapGenQueue() {
        return snapGenQueue;
    }

    public int getSnapPopQueue() {
        return snapPopQueue;
    }

    public int getSnapTerrainGenCount() {
        return snapTerrainGenCount;
    }

    public int getSnapPopCount() {
        return snapPopCount;
    }

    public int getSnapNeighborFills() {
        return snapNeighborFills;
    }

    public int getSnapStuckCount() {
        return snapStuckCount;
    }

    public int getSnapResendRetries() {
        return snapResendRetries;
    }

    public double getSnapBudgetUsedMs() {
        return snapBudgetUsedMs;
    }

    public double getSnapBudgetAvailMs() {
        return snapBudgetAvailMs;
    }

    public int getSnapBlockedLoads() {
        return snapBlockedLoads;
    }

    public int getSnapPipelineBacklog() {
        return snapPipelineBacklog;
    }

    public boolean isSnapInDebt() {
        return snapInDebt;
    }

    public double getTerrainGenEmaMs() {
        return budget.getTerrainGenEmaMs();
    }

    public double getPopulateEmaMs() {
        return budget.getPopulateEmaMs();
    }

    public double getDebtMs() {
        return budget.getDebtMs();
    }
}
