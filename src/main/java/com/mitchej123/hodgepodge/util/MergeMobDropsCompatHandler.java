package com.mitchej123.hodgepodge.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.util.FakePlayer;

import cpw.mods.fml.common.Loader;
import fox.spiteful.avaritia.items.ItemMatterCluster;
import fox.spiteful.avaritia.items.ItemStackWrapper;
import mods.battlegear2.api.EnchantmentHelper;
import mods.battlegear2.api.core.IBattlePlayer;
import mods.battlegear2.enchantments.BaseEnchantment;
import xonin.backhand.api.core.BackhandUtils;

public class MergeMobDropsCompatHandler {

    // Bow luck equivalent compatible with MixinEntityLivingBase_MergeMobDrops
    public static void applyBowLuck(DamageSource source, List<ItemStack> drops) {
        if (!Loader.isModLoaded("battlegear2")) {
            return;
        }

        if (BaseEnchantment.bowLoot.isPresent() && source.isProjectile()
                && source.getEntity() instanceof EntityLivingBase) {
            EntityLivingBase shooter = (EntityLivingBase) source.getEntity();
            ItemStack stack = shooter.getHeldItem();
            int lvl = EnchantmentHelper.getEnchantmentLevel(BaseEnchantment.bowLoot, stack);

            if (lvl <= 0 && shooter instanceof IBattlePlayer && !(shooter instanceof FakePlayer)) {
                EntityPlayer player = (EntityPlayer) shooter;
                stack = BackhandUtils.getOffhandItem(player);
                lvl = EnchantmentHelper.getEnchantmentLevel(BaseEnchantment.bowLoot, stack);
            }

            if (lvl > 0) {
                for (ItemStack drop : drops) {
                    /*
<<<<<<< HEAD
                     * This is not a typo nor a different logic, since the original bow luck logic exploits the fact
                     * that the vanilla dispatches item entites only with stackSize == 1, but now the drops have been
                     * merged by the mode == 1 (FORCE_MERGE_INTO_ONE) at the time when the enchantment applies
=======
                     * This is not a typo nor a different logic, since the bow luck multiplies mob drops up to its max
                     * stack size, and drops have been merged by the mode == 1 (FORCE_MERGE_INTO_ONE) at the time when
                     * the enchantment applies
>>>>>>> 788be5e9ed7467381286ae22d9b54ab0ed911d94
                     */
                    if (drop != null && drop.getMaxStackSize() >= lvl + 1) {
                        drop.stackSize *= lvl + 1;
                    }
                }
            }
        }
    }

    // Pack mob drops into Avaritia matter clusters
    public static void convertToClusters(List<ItemStack> drops) {
        /*
         * 256 is to prevent memory reallocations as the array grows. Don't ask how one can get this high...actually
         * there's a hint right above hehe
         */
        List<ItemStack> result = new ArrayList<>(256);
        Map<ItemStackWrapper, Integer> currentMap = new HashMap<>();
        int currentCount = 0; // Tracks how many total items have been packed into the current cluster so far

        for (ItemStack drop : drops) {
            int remainingInStack = drop.stackSize;
            drop.stackSize = 1;
            ItemStackWrapper wrap = new ItemStackWrapper(drop);
            drop.stackSize = remainingInStack;

            while (remainingInStack > 0) {
                int spaceLeft = ItemMatterCluster.MAX_NORMAL_CAPACITY - currentCount;
                int take = Math.min(remainingInStack, spaceLeft);

                if (take > 0) {
                    Integer existing = currentMap.get(wrap);
                    currentMap.put(wrap, existing == null ? take : existing + take);
                    currentCount += take;
                    remainingInStack -= take;
                }

                if (currentCount >= ItemMatterCluster.MAX_NORMAL_CAPACITY) {
                    result.add(ItemMatterCluster.makeCluster(currentMap));
                    currentMap.clear();
                    currentCount = 0;
                }
            }
        }

        /*
         * Apply mode == 1 (FORCE_MERGE_INTO_ONE) to the remainder (leftover items that didn't reach
         * MAX_NORMAL_CAPACITY)
         */
        if (!currentMap.isEmpty()) {
            for (Map.Entry<ItemStackWrapper, Integer> entry : currentMap.entrySet()) {
                ItemStack remainderStack = entry.getKey().stack.copy();
                remainderStack.stackSize = entry.getValue();
                result.add(remainderStack);
            }
        }
        drops.clear();
        drops.addAll(result);
    }
}
