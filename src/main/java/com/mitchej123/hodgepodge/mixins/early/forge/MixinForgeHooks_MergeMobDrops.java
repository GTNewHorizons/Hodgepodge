package com.mitchej123.hodgepodge.mixins.early.forge;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDropsEvent;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.mitchej123.hodgepodge.config.TweaksConfig;
import com.mitchej123.hodgepodge.util.MergeMobDropsCompatHandler;
import com.mitchej123.hodgepodge.util.MobDropsContainerContext;

import cpw.mods.fml.common.Loader;

@Mixin(ForgeHooks.class)
public abstract class MixinForgeHooks_MergeMobDrops {

    /**
     * @author nimuck
     * @reason Merge and dispatch mob drops. See TweaksConfig.mergeMobDrops and Mixins.MERGE_MOB_DROPS for more info
     */
    @Inject(method = "onLivingDrops", at = @At("HEAD"), cancellable = true, remap = false)
    private static void hodgepodge$mergeMobDrops(EntityLivingBase entity, DamageSource source,
            ArrayList<EntityItem> unusedDrops, int lootingLevel, boolean recentlyHit, int specialDropValue,
            CallbackInfoReturnable<Boolean> cir) {
        List<ItemStack> drops = MobDropsContainerContext.end();

        if (drops == null || drops.isEmpty()) {
            cir.setReturnValue(true);
            return;
        }

        /* ====== Compatibility patches start ===== */

        if (Loader.isModLoaded("battlegear2")) {
            // Apply Battlegear2 bow luck enchantment equivalent
            MergeMobDropsCompatHandler.applyBowLuck(source, drops);
        }
        /* ====== Compatibility patches end ===== */

        mergeMobDrops(drops);

        // Convert to entities
        ArrayList<EntityItem> realDrops = new ArrayList<>(drops.size());
        for (ItemStack drop : drops) {
            if (drop != null) {
                EntityItem entityItem = new EntityItem(entity.worldObj, entity.posX, entity.posY, entity.posZ, drop);
                entityItem.delayBeforeCanPickup = 10;
                realDrops.add(entityItem);
            }
        }
        drops.clear();

        // Manually fire a LivingDropsEvent as the bottommost cir.setReturnValue(true) cancels the origianl
        LivingDropsEvent event = new LivingDropsEvent(
                entity,
                source,
                realDrops,
                lootingLevel,
                recentlyHit,
                specialDropValue);
        boolean eventCanceled = MinecraftForge.EVENT_BUS.post(event);

        // Normally Entity.entityDropItem dispatches them, but currently it's nuked by MixinEntity_MergeMobDrops
        if (!eventCanceled) {
            for (EntityItem entityItem : realDrops) {
                if (entityItem != null) {
                    entity.worldObj.spawnEntityInWorld(entityItem);
                }
            }
        }

        cir.setReturnValue(true);
    }

    private static void mergeMobDrops(List<ItemStack> drops) {
        int mode = TweaksConfig.mergeMobDropsMode;

        // FORCE_MERGE_INTO_ONE
        if (mode == 1) return; // They've already handled in MixinEntityLiving_MergeMobDrops or Mixin<EntityName>

        // Check if the threshold is breached
        boolean isAboveThreshold = false;
        int totalCount = 0;
        int threshold = TweaksConfig.mergeMobDropsModeHybridThreshold;
        for (ItemStack drop : drops) {
            if (drop == null) continue;

            totalCount += drop.stackSize;

            if (totalCount >= threshold) {
                isAboveThreshold = true;
                break; // Early escape
            }
        }

        if (isAboveThreshold) {
            // HYBRID_AVARITIA
            if (mode == 3 && Loader.isModLoaded("Avaritia")) {
                MergeMobDropsCompatHandler.convertToClusters(drops, threshold);
                return;
            }

            // HYBRID || HYBRID_AVARITIA without Avaritia, choosing FORCE_MERGE_INTO_ONE
            if (mode == 2 || mode == 3) return;
        }

        // RESPECT_MAX_STACK_SIZE || HYBRID choosing it || HYBRID_AVARITIA choosing it
        if (mode == 0 || mode == 2 || mode == 3) {
            /*
             * Predefining the size of the array is to prevent memory reallocations it grows. Estimation logic:
             * threshold/64 (stackables) + 4 (equipments) + 2 (several rare drops)
             */
            List<ItemStack> fragmentedDrops = new ArrayList<>(threshold / 64 + 6);

            for (ItemStack drop : drops) {
                if (drop == null) continue;

                int remaining = drop.stackSize;
                int maxStackSize = drop.getMaxStackSize();

                int currentBatchSize;
                while (remaining > 0) {
                    currentBatchSize = Math.min(remaining, maxStackSize);
                    ItemStack splitStack = drop.copy();
                    splitStack.stackSize = currentBatchSize;

                    fragmentedDrops.add(splitStack);
                    remaining -= currentBatchSize;
                }
            }
            drops.clear();
            drops.addAll(fragmentedDrops);
        }
    }
}
