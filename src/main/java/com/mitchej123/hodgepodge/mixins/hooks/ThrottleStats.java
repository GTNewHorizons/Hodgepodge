package com.mitchej123.hodgepodge.mixins.hooks;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.mitchej123.hodgepodge.config.DebugConfig;

public class ThrottleStats {

    private static PrintWriter detailWriter;
    private static boolean detailInitAttempted;
    private static PrintWriter summaryWriter;
    private static boolean summaryInitAttempted;
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm:ss.SSS");

    private static PrintWriter getDetailWriter() {
        if (!detailInitAttempted) {
            detailInitAttempted = true;
            try {
                detailWriter = new PrintWriter(new FileWriter("logs/hodgepodge-throttle-stats.log", true), true);
            } catch (IOException ignored) {}
        }
        return detailWriter;
    }

    private static PrintWriter getSummaryWriter() {
        if (!summaryInitAttempted) {
            summaryInitAttempted = true;
            try {
                summaryWriter = new PrintWriter(new FileWriter("logs/hodgepodge-chunkgen-summary.log", true), true);
                summaryWriter.println(
                        "# timestamp dim ticks terrain pop blocked deferred fills stuck resend "
                                + "peakUsedMs budgetMs peakGenQ peakPopQ peakPipe avgTerMs avgPopMs peakDebtMs debtTicks");
            } catch (IOException ignored) {}
        }
        return summaryWriter;
    }

    public static void logDetail(int dimId, int genQueue, int popQueue, int terrainGenCount, double terrainGenMs,
            int popCount, double popMs, double debtMs, boolean inDebt, int deferredCount, int passthroughCount,
            double emaTerrainMs, double emaPopMs, double budgetUsedMs, double budgetAvailMs, int blockedLoads,
            int terrainStatusCount, int fills, int stuck, int resendRetries) {
        if (!DebugConfig.chunkGenLogging) return;
        final PrintWriter w = getDetailWriter();
        if (w != null) {
            w.printf(
                    "%s dim=%d genQ=%d popQ=%d pipeline=%d terrain=%d/%.1fms pop=%d/%.1fms debt=%.1fms"
                            + " avg=t:%.1fms/p:%.1fms used=%.1f/%.1fms blocked=%d deferred=%d pt=%d"
                            + " fills=%d stuck=%d resend=%d%s%n",
                    TIME_FORMAT.format(new Date()),
                    dimId,
                    genQueue,
                    popQueue,
                    terrainStatusCount,
                    terrainGenCount,
                    terrainGenMs,
                    popCount,
                    popMs,
                    debtMs,
                    emaTerrainMs,
                    emaPopMs,
                    budgetUsedMs,
                    budgetAvailMs,
                    blockedLoads,
                    deferredCount,
                    passthroughCount,
                    fills,
                    stuck,
                    resendRetries,
                    inDebt ? " DEBT" : "");
        }
    }

    public static void logForceProcess(int dimId, int cx, int cz, int ageTicks, int neighborsFilled) {
        if (!DebugConfig.chunkGenLogging) return;
        final PrintWriter w = getDetailWriter();
        if (w != null) {
            w.printf(
                    "%s dim=%d FORCE chunk=[%d,%d] age=%dt neighbors=%d%n",
                    TIME_FORMAT.format(new Date()),
                    dimId,
                    cx,
                    cz,
                    ageTicks,
                    neighborsFilled);
        }
    }

    public static void logSummary(int dimId, int ticks, int terrainTotal, int popTotal, int blockedTotal,
            int deferredTotal, int fillsTotal, int stuckTotal, int resendTotal, double peakUsedMs, double budgetMs,
            int peakGenQ, int peakPopQ, int peakPipe, double emaTerrainMs, double emaPopMs, double peakDebtMs,
            int debtTicks) {
        if (!DebugConfig.chunkGenLogging) return;
        final PrintWriter sw = getSummaryWriter();
        if (sw != null) {
            sw.printf(
                    "%s dim=%d ticks=%d terrain=%d pop=%d blocked=%d deferred=%d fills=%d stuck=%d resend=%d"
                            + " peakUsed=%.1fms budget=%.1fms peakGenQ=%d peakPopQ=%d peakPipe=%d"
                            + " avg=t:%.1fms/p:%.1fms peakDebt=%.1fms debtTicks=%d%n",
                    TIME_FORMAT.format(new Date()),
                    dimId,
                    ticks,
                    terrainTotal,
                    popTotal,
                    blockedTotal,
                    deferredTotal,
                    fillsTotal,
                    stuckTotal,
                    resendTotal,
                    peakUsedMs,
                    budgetMs,
                    peakGenQ,
                    peakPopQ,
                    peakPipe,
                    emaTerrainMs,
                    emaPopMs,
                    peakDebtMs,
                    debtTicks);
        }
    }
}
