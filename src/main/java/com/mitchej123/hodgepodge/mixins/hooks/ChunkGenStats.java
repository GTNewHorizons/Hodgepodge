package com.mitchej123.hodgepodge.mixins.hooks;

public class ChunkGenStats {

    private final int dimId;
    private final ChunkGenBudget budget;

    private int deferredCount;
    private int directCount;
    private int neighborFillCount;
    private int stuckCount;
    private int resendRetryCount;

    private volatile int snapGenQueue, snapPopQueue, snapPipelineBacklog;
    private volatile int snapTerrainGenCount, snapPopCount;
    private volatile int snapNeighborFills, snapStuckCount, snapResendRetries;
    private volatile double snapBudgetUsedMs, snapBudgetAvailMs;
    private volatile int snapBlockedLoads;
    private volatile boolean snapInDebt;

    private long periodStartNanos;
    private int periodTicks;
    private int periodTerrainGenTotal, periodPopTotal, periodBlockedTotal;
    private int periodDeferredTotal, periodFillsTotal, periodStuckTotal, periodResendTotal;
    private double periodBudgetUsedMaxMs, periodBudgetAvailMs;
    private int periodGenQueueMax, periodPopQueueMax;
    private double periodDebtMaxMs;
    private int periodDebtTicks, periodPipelineMax;
    private boolean periodHadActivity;
    private static final long SUMMARY_INTERVAL_NS = 500_000_000L;

    ChunkGenStats(int dimId, ChunkGenBudget budget) {
        this.dimId = dimId;
        this.budget = budget;
        this.periodStartNanos = System.nanoTime();
    }

    public void incrementDeferred() {
        deferredCount++;
    }

    public void incrementDirect() {
        directCount++;
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
        final int deferred = resetDeferredCount();
        final int passthrough = resetDirectCount();

        final boolean hasActivity = terrainGenCount > 0 || popCount > 0
                || fills > 0
                || deferred > 0
                || passthrough > 0
                || blockedLoads > 0
                || resendRetries > 0;

        if (hasActivity) {
            ThrottleStats.logDetail(
                    dimId,
                    genQueue,
                    popQueue,
                    terrainGenCount,
                    terrainNanos / 1_000_000.0,
                    popCount,
                    popNanos / 1_000_000.0,
                    budget.getDebtMs(),
                    inDebt,
                    deferred,
                    passthrough,
                    budget.getTerrainGenEmaMs(),
                    budget.getPopulateEmaMs(),
                    budgetUsedMs,
                    budgetAvailMs,
                    blockedLoads,
                    terrainStatusCount,
                    fills,
                    stuck,
                    resendRetries);
        }

        updateSnapshot(
                genQueue,
                popQueue,
                terrainGenCount,
                popCount,
                fills,
                stuck,
                resendRetries,
                budgetUsedMs,
                budgetAvailMs,
                blockedLoads,
                inDebt,
                terrainStatusCount);

        accumulateSummary(
                genQueue,
                popQueue,
                terrainGenCount,
                popCount,
                blockedLoads,
                deferred,
                fills,
                stuck,
                resendRetries,
                budgetUsedMs,
                budgetAvailMs,
                terrainStatusCount,
                inDebt);
    }

    private int resetDeferredCount() {
        final int c = deferredCount;
        deferredCount = 0;
        return c;
    }

    private int resetDirectCount() {
        final int c = directCount;
        directCount = 0;
        return c;
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

    private void updateSnapshot(int genQueue, int popQueue, int terrainGenCount, int popCount, int neighborFills,
            int stuckCnt, int resendRetries, double budgetUsedMs, double budgetAvailMs, int blockedLoads,
            boolean inDebt, int pipelineBacklog) {
        this.snapGenQueue = genQueue;
        this.snapPopQueue = popQueue;
        this.snapPipelineBacklog = pipelineBacklog;
        this.snapTerrainGenCount = terrainGenCount;
        this.snapPopCount = popCount;
        this.snapNeighborFills = neighborFills;
        this.snapStuckCount = stuckCnt;
        this.snapResendRetries = resendRetries;
        this.snapBudgetUsedMs = budgetUsedMs;
        this.snapBudgetAvailMs = budgetAvailMs;
        this.snapBlockedLoads = blockedLoads;
        this.snapInDebt = inDebt;
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

    private void accumulateSummary(int genQueue, int popQueue, int terrainGenCount, int popCount, int blockedLoads,
            int deferred, int fills, int stuck, int resendRetries, double budgetUsedMs, double budgetAvailMs,
            int terrainStatusCount, boolean inDebt) {
        periodTicks++;
        if (terrainGenCount > 0 || popCount > 0 || fills > 0 || deferred > 0 || resendRetries > 0 || blockedLoads > 0) {
            periodHadActivity = true;
        }
        periodTerrainGenTotal += terrainGenCount;
        periodPopTotal += popCount;
        periodBlockedTotal += blockedLoads;
        periodDeferredTotal += deferred;
        periodFillsTotal += fills;
        periodStuckTotal += stuck;
        periodResendTotal += resendRetries;
        periodBudgetUsedMaxMs = Math.max(periodBudgetUsedMaxMs, budgetUsedMs);
        periodBudgetAvailMs = budgetAvailMs;
        periodGenQueueMax = Math.max(periodGenQueueMax, genQueue);
        periodPopQueueMax = Math.max(periodPopQueueMax, popQueue);
        periodDebtMaxMs = Math.max(periodDebtMaxMs, budget.getDebtMs());
        if (inDebt) periodDebtTicks++;
        periodPipelineMax = Math.max(periodPipelineMax, terrainStatusCount);

        final long now = System.nanoTime();
        if (now - periodStartNanos >= SUMMARY_INTERVAL_NS) {
            if (periodHadActivity) {
                ThrottleStats.logSummary(
                        dimId,
                        periodTicks,
                        periodTerrainGenTotal,
                        periodPopTotal,
                        periodBlockedTotal,
                        periodDeferredTotal,
                        periodFillsTotal,
                        periodStuckTotal,
                        periodResendTotal,
                        periodBudgetUsedMaxMs,
                        periodBudgetAvailMs,
                        periodGenQueueMax,
                        periodPopQueueMax,
                        periodPipelineMax,
                        budget.getTerrainGenEmaMs(),
                        budget.getPopulateEmaMs(),
                        periodDebtMaxMs,
                        periodDebtTicks);
            }
            resetSummary();
        }
    }

    private void resetSummary() {
        periodStartNanos = System.nanoTime();
        periodTicks = 0;
        periodTerrainGenTotal = 0;
        periodPopTotal = 0;
        periodBlockedTotal = 0;
        periodDeferredTotal = 0;
        periodFillsTotal = 0;
        periodStuckTotal = 0;
        periodResendTotal = 0;
        periodBudgetUsedMaxMs = 0;
        periodGenQueueMax = 0;
        periodPopQueueMax = 0;
        periodDebtMaxMs = 0;
        periodDebtTicks = 0;
        periodPipelineMax = 0;
        periodHadActivity = false;
    }
}
