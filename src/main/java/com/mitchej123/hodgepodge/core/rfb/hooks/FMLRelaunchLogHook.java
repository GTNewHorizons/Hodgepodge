package com.mitchej123.hodgepodge.core.rfb.hooks;

import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;

/**
 * Cached {@code LogManager.getLogger} for {@code FMLRelaunchLog.log}
 */
public final class FMLRelaunchLogHook {

    static final String FQCN = "cpw.mods.fml.relauncher.FMLRelaunchLog";
    static final int CAP = 4096;

    private static final ConcurrentHashMap<String, Entry> CACHE = new ConcurrentHashMap<>();

    private FMLRelaunchLogHook() {}

    // FMLRelaunchLog is the sole caller and shares the RFB system loader with this class;
    // passing it keeps the miss path identical to a stock LogManager.getLogger.
    public static Logger getLogger(String name, Class<?> caller) {
        if (name == null) name = FQCN;
        final Entry e = CACHE.get(name);
        if (e != null && live(e.ctx)) return e.logger;
        final var ctx = LogManager.getContext(caller.getClassLoader(), false);
        if (!(ctx instanceof LoggerContext core)) return ctx.getLogger(name);
        final Logger logger = core.getLogger(name);
        if (e != null || CACHE.size() < CAP) CACHE.put(name, new Entry(logger, core));
        return logger;
    }

    private static boolean live(LoggerContext ctx) {
        final LoggerContext.Status s = ctx.getStatus();
        return s != LoggerContext.Status.STOPPING && s != LoggerContext.Status.STOPPED;
    }

    static int cacheSize() {
        return CACHE.size();
    }

    static void clear() {
        CACHE.clear();
    }

    private static final class Entry {

        final Logger logger;
        final LoggerContext ctx;

        Entry(Logger logger, LoggerContext ctx) {
            this.logger = logger;
            this.ctx = ctx;
        }
    }
}
