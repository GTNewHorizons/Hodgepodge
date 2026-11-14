package com.mitchej123.hodgepodge.util;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;

// Used by Entity.dropItem and EntityLivingBase.onDeath to collect Itemstacks a mob has dropped
public class MobDropsContainerContext {

    private static List<ItemStack> drops = null;

    public static void start() {
        drops = new ArrayList<>();
    }

    public static boolean isCollecting() {
        return (drops != null);
    }

    public static void add(ItemStack stack) {
        drops.add(stack);
    }

    public static List<ItemStack> end() {
        List<ItemStack> list = drops;
        drops = null;
        return list;
    }
}
