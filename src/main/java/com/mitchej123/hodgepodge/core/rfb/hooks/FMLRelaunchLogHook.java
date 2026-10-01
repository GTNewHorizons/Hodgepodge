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
    public static final int CAP = 4096;

    private static final ConcurrentHashMap<String, Entry> CACHE = new ConcurrentHashMap<>();

    private FMLRelaunchLogHook() {}

    public static Logger getLogger(String name, Class<?> caller) {
        if (name == null) name = FQCN;
        final ClassLoader loader = caller.getClassLoader();
        final Entry e = CACHE.get(name);
        if (e != null && e.loader == loader && live(e.ctx)) return e.logger;
        final var ctx = LogManager.getContext(loader, false);
        if (!(ctx instanceof LoggerContext core)) return ctx.getLogger(name);
        final Logger logger = core.getLogger(name);
        if (e != null || CACHE.size() < CAP) CACHE.put(name, new Entry(logger, core, loader));
        return logger;
    }

    private static boolean live(LoggerContext ctx) {
        final LoggerContext.Status s = ctx.getStatus();
        return s != LoggerContext.Status.STOPPING && s != LoggerContext.Status.STOPPED;
    }

    public static int cacheSize() {
        return CACHE.size();
    }

    private static final class Entry {

        final Logger logger;
        final LoggerContext ctx;
        final ClassLoader loader;

        Entry(Logger logger, LoggerContext ctx, ClassLoader loader) {
            this.logger = logger;
            this.ctx = ctx;
            this.loader = loader;
        }
    }
}
