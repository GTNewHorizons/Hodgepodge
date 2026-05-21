package com.mitchej123.hodgepodge.mixins.hooks;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public final class ASMDataStringPooler {

    private static ConcurrentHashMap<String, String> pool = new ConcurrentHashMap<>();

    public static String intern(String s) {
        if (s == null) return null;
        return pool.computeIfAbsent(s, Function.identity());
    }

    public static void free() {
        pool = new ConcurrentHashMap<>();
    }
}
