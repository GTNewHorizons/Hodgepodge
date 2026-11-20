package com.mitchej123.hodgepodge.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.item.ItemStack;

// Used by MixinEntityLivingBase_MergeMobDrops to collect Itemstacks a mob has dropped
public class MobDropsContainerContext {

    private static List<ItemStack> drops = null;
    // Mainly to deal with TC4 calling dropItemWithOffset(item, count, offsetY). No other mods use it >:(
    private static float[] offsets = new float[16];
    private static boolean hasNonZeroOffset = false;

    public static void start() {
        drops = new ArrayList<>();
        hasNonZeroOffset = false;
    }

    public static boolean isCollecting() {
        return drops != null;
    }

    public static void add(ItemStack stack, float offsetY) {
        int index = drops.size();
        drops.add(stack);

        if (offsetY != 0.0F) {
            boolean outOfBound = index >= offsets.length;

            if (!hasNonZeroOffset) {
                hasNonZeroOffset = true;
                if (outOfBound) {
                    offsets = new float[index + 1];
                } else {
                    Arrays.fill(offsets, 0, index, 0.0F);
                }
            } else if (outOfBound) {
                offsets = Arrays.copyOf(offsets, Math.max(offsets.length * 2, index + 1));
            }
            offsets[index] = offsetY;
        }
    }

    public static boolean hasNonZeroOffset() {
        return hasNonZeroOffset;
    }

    public static float getOffset(int index) {
        return hasNonZeroOffset ? offsets[index] : 0.0F;
    }

    public static void ensureOffsetCapacity(int size) {
        if (offsets.length < size) {
            offsets = Arrays.copyOf(offsets, size);
        }
    }

    public static void setOffset(int index, float offsetY) {
        offsets[index] = offsetY;
    }

    public static void clearOffsets() {
        hasNonZeroOffset = false;
    }

    public static List<ItemStack> end() {
        List<ItemStack> list = drops;
        drops = null;
        return list;
    }
}
