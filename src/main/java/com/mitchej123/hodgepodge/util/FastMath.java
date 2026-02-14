package com.mitchej123.hodgepodge.util;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;

/**
 * Fast Math
 */
public final class FastMath {

    private static final MethodHandle FAST_ATAN2;

    static {
        MethodHandle mh = null;
        try {
            Method m = org.joml.Math.class.getDeclaredMethod("fastAtan2", double.class, double.class);
            m.setAccessible(true);
            mh = MethodHandles.lookup().unreflect(m);
        } catch (ReflectiveOperationException ignored) {}
        FAST_ATAN2 = mh;
    }

    public static double atan2(double y, double x) {
        if (FAST_ATAN2 == null) return Math.atan2(y, x);
        try {
            return (double) FAST_ATAN2.invokeExact(y, x);
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    private FastMath() {}
}
