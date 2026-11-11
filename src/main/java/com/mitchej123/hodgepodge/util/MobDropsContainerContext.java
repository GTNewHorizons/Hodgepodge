package com.mitchej123.hodgepodge.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.item.ItemStack;

// Used by MixinEntityLivingBase_MergeMobDrops to collect Itemstacks a mob has dropped
public class MobDropsContainerContext {

    public List<ItemStack> drops = new ArrayList<>();
    // Mainly to deal with TC4 calling dropItemWithOffset(item, count, offsetY). No other mod uses it >:(
    public float[] offsets = new float[4];
    public boolean hasNonZeroOffset = false;

    public void add(ItemStack stack, float offsetY) {
        drops.add(stack);

        if (offsetY != 0.0F) {
            if (!hasNonZeroOffset) hasNonZeroOffset = true;

            int index = drops.size();
            if (offsets.length <= index) {
                offsets = Arrays.copyOf(offsets, Math.max(offsets.length * 2, index + 1));
            }
            offsets[index] = offsetY;
        }
    }

    public void ensureOffsetCapacity(int size) {
        if (offsets.length < size) {
            offsets = Arrays.copyOf(offsets, size);
        }
    }
}
