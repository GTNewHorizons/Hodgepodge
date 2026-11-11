package com.mitchej123.hodgepodge.core.rfb.hooks;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mitchej123.hodgepodge.core.shared.FileLogger;

@SuppressWarnings("unused")
public class EnumValuesHook {

    private static final Logger logger = LogManager.getLogger("EnumValuesDebug");
    private static final Map<Class<?>, Integer> counts = new ConcurrentHashMap<>();
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
            List<Map.Entry<Class<?>, Integer>> sorted = counts.entrySet().stream()
                    .sorted(Comparator.comparingInt(Map.Entry::getValue)).collect(Collectors.toList());
            Collections.reverse(sorted);
            sorted.forEach(entry -> {
                final String name = entry.getKey().getName();
                logger.log(name + ";" + entry.getValue());
            });
        } catch (Throwable ignored) {}
    }
}
