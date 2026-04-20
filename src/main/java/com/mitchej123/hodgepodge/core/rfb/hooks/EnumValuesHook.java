package com.mitchej123.hodgepodge.core.rfb.hooks;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mitchej123.hodgepodge.core.shared.FileLogger;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

@SuppressWarnings("unused")
public class EnumValuesHook {

    private static final Logger logger = LogManager.getLogger("EnumValuesDebug");
    private static final Object2IntOpenHashMap<Class<?>> counts = new Object2IntOpenHashMap<>();
    private static final boolean STACKTRACE = Boolean.getBoolean("hodgepodge.logStacktraceEnumValues");
    public static final int THRESHOLD = Integer.getInteger("hodgepodge.logIntervalEnumValues", 500);

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(EnumValuesHook::printResults));
    }

    public static boolean shouldLog(int counter) {
        return counter >= THRESHOLD;
    }

    public static void logMethod(Class<?> clazz, int total) {
        counts.put(clazz, total);
        String msg = String
                .format("%s.values() copied %d entries, total %d copies!", clazz.getName(), THRESHOLD, total);
        if (STACKTRACE) {
            logger.warn(msg, new Exception());
        } else {
            logger.warn(msg);
        }
    }

    private static void printResults() {
        try (FileLogger logger = new FileLogger("EnumValuesDebug.csv")) {
            logger.log("Enum;Count");
            List<Object2IntMap.Entry<Class<?>>> sorted = counts.object2IntEntrySet().stream()
                    .sorted(Comparator.comparingInt(Object2IntMap.Entry::getIntValue)).collect(Collectors.toList());
            Collections.reverse(sorted);
            sorted.forEach(entry -> {
                final String name = entry.getKey().getName();
                logger.log(name + ";" + entry.getIntValue());
            });
        } catch (Throwable ignored) {}
    }
}
