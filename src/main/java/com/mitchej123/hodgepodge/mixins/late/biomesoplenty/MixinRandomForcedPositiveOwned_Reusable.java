package com.mitchej123.hodgepodge.mixins.late.biomesoplenty;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import com.mitchej123.hodgepodge.mixins.interfaces.ReusableRandom;

import biomesoplenty.common.utils.RandomForcedPositiveOwned;

@Mixin(value = RandomForcedPositiveOwned.class, remap = false)
public abstract class MixinRandomForcedPositiveOwned_Reusable extends Random implements ReusableRandom {

    @Shadow
    @Final
    @Mutable
    private Random parent;

    @Unique
    private static final MethodHandle hodgepodge$seedGetter;

    static {
        try {
            Field seedField = Random.class.getDeclaredField("seed");
            seedField.setAccessible(true);
            hodgepodge$seedGetter = MethodHandles.lookup().unreflectGetter(seedField);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Failed to resolve Random.seed accessor", e);
        }
    }

    @Override
    public void hodgepodge$updateParent(Random newParent) {
        this.parent = newParent;
        try {
            long seedValue = ((AtomicLong) hodgepodge$seedGetter.invokeExact(newParent)).get();
            this.setSeed(seedValue);
        } catch (Throwable e) {
            throw new RuntimeException("Failed to read Random seed", e);
        }
    }
}
