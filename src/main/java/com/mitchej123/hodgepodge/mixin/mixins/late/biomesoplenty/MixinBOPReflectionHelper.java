package com.mitchej123.hodgepodge.mixin.mixins.late.biomesoplenty;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import com.mitchej123.hodgepodge.util.FinalValueHelper;

import biomesoplenty.common.helpers.BOPReflectionHelper;

@Mixin(BOPReflectionHelper.class)
public class MixinBOPReflectionHelper {

    /**
     * @author eigenraven
     * @reason Make it compatible with Java 12+.
     */
    @Overwrite(remap = false)
    // Decompiler struggles with varargs, hence the "transient" warning in IJ
    public static <T, E> void setPrivateFinalValue(Class<? super T> classToAccess, T instance, E value,
            String... fieldNames) {
        FinalValueHelper.setPrivateFinalValue(classToAccess, instance, value, fieldNames);
    }
}
