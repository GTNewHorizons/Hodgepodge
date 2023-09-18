package com.mitchej123.hodgepodge.mixin.mixins.late.lotr;

import static com.mitchej123.hodgepodge.util.FinalValueHelper.setPrivateFinalValue;

import java.lang.reflect.Field;

import org.apache.logging.log4j.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import lotr.common.LOTRReflection;
import lotr.common.util.LOTRLog;

@Mixin(value = LOTRReflection.class)
public class MixinRemoveUnlockFinalField {

    /**
     * @author Mist475
     * @reason This way of writing to final fields is broken in java 12+, this neutralises any call not patched
     */
    @Overwrite(remap = false)
    public static void unlockFinalField(Field f) {

    }

    /**
     * @author Mist475
     * @reason Set Values in j12+ proof way
     */
    @Overwrite(remap = false)
    // Decompiler struggles with varargs, hence the "transient" warning in IJ
    public static <T, E> void setFinalField(final Class<? super T> classToAccess, final T instance, final E value,
            String... fieldNames) {
        try {
            setPrivateFinalValue(classToAccess, instance, value, fieldNames);
        } catch (final Exception e) {
            LOTRLog.logger.log(Level.ERROR, "Unable to access static final field");
            throw e;
        }
    }

    /**
     * @author Mist475
     * @reason Set Values in j12+ proof way
     */
    @Overwrite(remap = false)
    public static <T, E> void setFinalField(final Class<? super T> classToAccess, final T instance, final E value,
            final Field f) {
        try {
            setPrivateFinalValue(classToAccess, instance, value, f.getName());
        } catch (final Exception e) {
            LOTRLog.logger.log(Level.ERROR, "Unable to access static final field");
            throw e;
        }
    }
}
