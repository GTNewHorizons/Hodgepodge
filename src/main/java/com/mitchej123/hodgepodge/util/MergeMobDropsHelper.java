package com.mitchej123.hodgepodge.util;

import java.util.ArrayList;
import java.util.Arrays;
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

public class MergeMobDropsHelper {

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
    private static void convertToClusters(List<ItemStack> drops) {
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

        if (!currentMap.isEmpty()) {
            result.add(ItemMatterCluster.makeCluster(currentMap));
        }
        drops.clear();
        drops.addAll(result);
    }

    public static void mergeMobDrops(MobDropsContainerContext ctx) {
        List<ItemStack> drops = ctx.drops;
        int mode = TweaksConfig.mergeMobDropsMode;

        // FORCE_MERGE_INTO_ONE
        if (mode == 1) return; // They've already handled in MixinEntityLiving_MergeMobDrops or Mixin<EntityName>

        // Check if the threshold is breached
        boolean isAboveThreshold = false;
        int totalCount = 0;
        int threshold = TweaksConfig.mergeMobDropsModeHybridThreshold;
        for (ItemStack drop : drops) {
            totalCount += drop.stackSize;

            if (totalCount >= threshold) {
                isAboveThreshold = true;
                break; // Early escape
            }
        }

        if (isAboveThreshold) {
            // HYBRID_AVARITIA
            if (mode == 3 && Loader.isModLoaded("Avaritia")) {
                MergeMobDropsHelper.convertToClusters(drops);
                Arrays.fill(ctx.offsets, 0.0f); // ignore offsets for clusters
                return;
            }

            // HYBRID || HYBRID_AVARITIA without Avaritia, choosing FORCE_MERGE_INTO_ONE
            if (mode == 2 || mode == 3) return;
        }

        // RESPECT_MAX_STACK_SIZE || HYBRID choosing it || HYBRID_AVARITIA choosing it
        if (mode == 0 || mode == 2 || mode == 3) {
            boolean hasOffsets = ctx.hasNonZeroOffset;
            int maxDropSize = threshold / 64 + 6;
            /*
             * Predefining the size of the array is to prevent memory reallocations it grows. Estimation logic:
             * threshold/64 (stackables) + 4 (equipments) + 2 (several rare drops)
             */
            List<ItemStack> sDrops = new ArrayList<>(maxDropSize); // s is for 'split'
            float[] sOffsets = hasOffsets ? new float[maxDropSize] : null;
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
                    if (hasOffsets) {
                        if (sOffsets.length <= sIndex) {
                            sOffsets = Arrays.copyOf(sOffsets, sOffsets.length * 2);
                        }
                        sOffsets[sIndex++] = ctx.offsets[i];
                    }
                    remaining -= currentBatchSize;
                }
            }

            drops.clear();
            drops.addAll(sDrops);

            if (hasOffsets) {
                ctx.offsets = sOffsets;
            }
        }
    }
}
