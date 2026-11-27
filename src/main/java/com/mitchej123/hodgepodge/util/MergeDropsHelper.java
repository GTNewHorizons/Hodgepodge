package com.mitchej123.hodgepodge.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.util.FakePlayer;

import com.mitchej123.hodgepodge.config.TweaksConfig;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Optional;
import fox.spiteful.avaritia.items.ItemMatterCluster;
import fox.spiteful.avaritia.items.ItemStackWrapper;
import mods.battlegear2.api.EnchantmentHelper;
import mods.battlegear2.api.core.IBattlePlayer;
import mods.battlegear2.enchantments.BaseEnchantment;
import xonin.backhand.api.core.BackhandUtils;

public class MergeDropsHelper {

    public static void mergeMobDrops(MobDropsContainerContext ctx) {
        int mode = TweaksConfig.mergeMobDropsMode;

        // FORCE_MERGE_INTO_ONE
        if (mode == 1) return; // Assuming dropItem/entityDropItem calls were already merged

        // Check if the threshold is breached
        boolean isAboveThreshold = false;
        int totalCount = 0;
        int threshold = TweaksConfig.mergeMobDropsModeHybridThreshold;
        for (ItemStack drop : ctx.drops) {
            totalCount += drop.stackSize;

            if (totalCount >= threshold) {
                isAboveThreshold = true;
                break; // Early escape
            }
        }

        if (isAboveThreshold) {
            // HYBRID_AVARITIA
            if (mode == 3 && Loader.isModLoaded("Avaritia")) {
                /*
                 * Ensuring the capacity is to prevent memory reallocations as the array grows. Don't ask how one can
                 * get this high...actually there's a hint right below hehe
                 */
                convertToClusters(ctx.drops, 256);
                Arrays.fill(ctx.offsets, 0.0f); // ignore offsets for clusters
                return;
            }

            // HYBRID || HYBRID_AVARITIA without Avaritia, choosing FORCE_MERGE_INTO_ONE
            if (mode == 2 || mode == 3) return;
        }

        // RESPECT_MAX_STACK_SIZE || HYBRID choosing it || HYBRID_AVARITIA choosing it
        if (mode == 0 || mode == 2 || mode == 3) {
            // Estimation logic: threshold/64 (stackables) + 4 (equipments) + 2 (several rare drops)
            respectMaxStackSize(ctx.drops, ctx.offsets, ctx.hasNonZeroOffset, threshold / 64 + 6);
        }

    }

    // Bow luck equivalent compatible with MixinEntityLivingBase_MergeMobDrops
    @Optional.Method(modid = "battlegear2")
    public static void applyBowLuck(DamageSource source, List<ItemStack> drops) {
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
                     * This is not a typo nor a different logic, since the original bow luck logic exploits the fact
                     * that the vanilla dispatches item entites only with stackSize == 1, but now the drops have been
                     * merged by the mode == 1 (FORCE_MERGE_INTO_ONE) at the time when the enchantment applies
                     */
                    if (drop != null && drop.getMaxStackSize() >= lvl + 1) {
                        drop.stackSize *= lvl + 1;
                    }
                }
            }
        }
    }

    // Pack mob drops into Avaritia matter clusters
    @Optional.Method(modid = "Avaritia")
    public static void convertToClusters(List<ItemStack> drops, int ensuredCapacity) {
        List<ItemStack> result = new ArrayList<>(ensuredCapacity);
        Map<ItemStackWrapper, Integer> currentMap = new HashMap<>();
        int currentCount = 0; // Tracks how many total items have been packed into the current cluster so far

        for (int i = 0, size = drops.size(); i < size; i++) {
            ItemStack drop = drops.get(i);
            int remainingInStack = drop.stackSize;
            drop.stackSize = 1;
            ItemStackWrapper wrap = new ItemStackWrapper(drop);
            drop.stackSize = remainingInStack;

            // Dedicated branch for pure clusters. Maximizing # of pure clusters enhances intuitiveness
            if (remainingInStack >= ItemMatterCluster.MAX_NORMAL_CAPACITY) {
                int pureClusterCount = remainingInStack / ItemMatterCluster.MAX_NORMAL_CAPACITY;
                ItemStack pureCluster = ItemMatterCluster.makeCluster(
                        Collections.singletonMap(wrap, ItemMatterCluster.MAX_NORMAL_CAPACITY));
                pureCluster.stackSize = pureClusterCount;
                result.add(pureCluster);
                remainingInStack %= ItemMatterCluster.MAX_NORMAL_CAPACITY;
            }

            if (remainingInStack > 0) {
                int spaceLeft = ItemMatterCluster.MAX_NORMAL_CAPACITY - currentCount;
                if (remainingInStack < spaceLeft) {
                    currentMap.merge(wrap, remainingInStack, Integer::sum);
                    currentCount += remainingInStack;
                } else {
                    currentMap.merge(wrap, spaceLeft, Integer::sum);
                    result.add(ItemMatterCluster.makeCluster(currentMap));
                    currentMap.clear();
                    currentCount = remainingInStack - spaceLeft;
                    if (currentCount > 0) {
                        currentMap.put(wrap, currentCount);
                    }
                }
            }
        }

        if (!currentMap.isEmpty()) { // Non-full cluster
            result.add(ItemMatterCluster.makeCluster(currentMap));
        }
        drops.clear();
        drops.addAll(result);
    }

    public static void respectMaxStackSize(List<ItemStack> drops, float[] offsets, boolean hasNonZeroOffset,
            int ensuredCapacity) {
        List<ItemStack> sDrops = new ArrayList<>(ensuredCapacity); // s is for 'split'
        float[] sOffsets = hasNonZeroOffset ? new float[ensuredCapacity] : null;
        int sIndex = 0;

        for (int i = 0, size = drops.size(); i < size; i++) {
            ItemStack drop = drops.get(i);

            int remaining = drop.stackSize;
            int maxStackSize = drop.getMaxStackSize();

            while (remaining > 0) {
                int currentBatchSize = Math.min(remaining, maxStackSize);
                ItemStack sStack = drop.copy();
                sStack.stackSize = currentBatchSize;

                sDrops.add(sStack);
                if (hasNonZeroOffset) {
                    if (sOffsets.length <= sIndex) {
                        sOffsets = Arrays.copyOf(sOffsets, sOffsets.length * 2);
                    }
                    sOffsets[sIndex++] = offsets[i];
                }
                remaining -= currentBatchSize;
            }
        }

        drops.clear();
        drops.addAll(sDrops);

        if (hasNonZeroOffset) {
            offsets = sOffsets;
        }
    }
}
