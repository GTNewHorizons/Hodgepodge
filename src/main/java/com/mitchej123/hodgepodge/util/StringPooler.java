package com.mitchej123.hodgepodge.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.Interner;
import com.google.common.collect.Interners;
import com.mitchej123.hodgepodge.config.TweaksConfig;

@SuppressWarnings("UnstableApiUsage")
public class StringPooler {

    public static StringPooler INSTANCE;
    private static final Logger LOGGER = LogManager.getLogger("HodgepodgeStringPooler");

    protected StringPooler() {}

    public static void setupPooler() {
        if (!TweaksConfig.enableStringPooling) {
            return;
        }
        if (INSTANCE != null) {
            throw new IllegalStateException("Pooler already set up");
        }
        LOGGER.info("Setting up string pooler: " + switch (TweaksConfig.stringPoolMode) {
            case 0 -> " (JVM)";
            case 1 -> " (Guava strong)";
            case 2 -> " (Guava weak)";
            default -> " (unknown)";
        });

        INSTANCE = switch (TweaksConfig.stringPoolMode) {
            case 0 -> new StringPooler();
            case 1 -> new GuavaPooler(true);
            case 2 -> new GuavaPooler(false);
            default -> throw new IllegalArgumentException("Invalid mode");
        };
    }

    public String getString(String s) {
        return s.intern();
    }

    static class GuavaPooler extends StringPooler {

        private final Interner<String> pool;

        protected GuavaPooler(boolean strong) {
            if (strong) {
                pool = Interners.newStrongInterner();
            } else {
                pool = Interners.newWeakInterner();
            }
        }

        @Override
        public String getString(String s) {
            return pool.intern(s);
        }
    }

}
