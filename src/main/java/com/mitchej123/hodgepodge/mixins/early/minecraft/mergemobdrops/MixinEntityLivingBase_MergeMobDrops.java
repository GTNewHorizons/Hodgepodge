package com.mitchej123.hodgepodge.mixins.early.minecraft.mergemobdrops;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mitchej123.hodgepodge.config.TweaksConfig;
import com.mitchej123.hodgepodge.util.MergeMobDropsCompatHandler;
import com.mitchej123.hodgepodge.util.MobDropsContainerContext;

import cpw.mods.fml.common.Loader;

// Merge mob drops with the help of MixinEntityLiving_MergeMobDrops. See TweaksConfig.mergeMobDrops
@Mixin(EntityLivingBase.class)
public abstract class MixinEntityLivingBase_MergeMobDrops extends Entity {

    @Inject(
            method = "onDeath",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityLivingBase;func_146066_aG()Z" // canDropLoot()
            ))
    public void hodgepodge$startCollectingMobDrops(DamageSource cause, CallbackInfo ci) {
        MobDropsContainerContext.start();
    }

    @Inject(
            method = "onDeath",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/World;setEntityState(Lnet/minecraft/entity/Entity;B)V"))
    public void hodgepodge$mergeAndDispatchMobDrops(DamageSource cause, CallbackInfo ci) {
        List<ItemStack> drops = MobDropsContainerContext.end();

        if (this.worldObj.isRemote || drops == null || drops.isEmpty()) {
            return;
        }

        /* ====== Compatibility patches start ===== */

        // Apply Battlegear2 bow loot enchantment equivalent
        MergeMobDropsCompatHandler.applyBowLuck(cause, drops);

        /* ====== Compatibility patches end ===== */

        mergeMobDrops(drops);

        // In vanilla Entity.entityDropItem does this, but currently it's nuked by MixinEntity_MergeMobDrops
        dispatchMobDrops(drops);
    }

    private void mergeMobDrops(List<ItemStack> drops) {
        int mode = TweaksConfig.mergeMobDropsMode;

        // FORCE_MERGE_INTO_ONE
        if (mode == 1) {
            return; // They've already handled in MixinEntityLiving_MergeMobDrops or Mixin<EntityName>
        }

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
                MergeMobDropsCompatHandler.convertToClusters(drops);
                return;
            }

            // HYBRID || HYBRID_AVARITIA without Avaritia, choosing FORCE_MERGE_INTO_ONE
            if (mode == 2 || mode == 3) return;
        }

        // RESPECT_MAX_STACK_SIZE || HYBRID choosing it
        if (mode == 0 || mode == 2) {
            /*
             * 72 is to prevent memory reallocations as the array grows. Estimation logic: 4096/64 (stackables) + 4
             * (equipments) + 4 (several rare drops)
             */
            List<ItemStack> fragmentedDrops = new ArrayList<>(72);

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

    private void dispatchMobDrops(List<ItemStack> drops) {
        for (ItemStack drop : drops) {
            if (drop != null) {
                EntityItem entityItem = new EntityItem(this.worldObj, this.posX, this.posY, this.posZ, drop);
                entityItem.delayBeforeCanPickup = 10;
                this.worldObj.spawnEntityInWorld(entityItem);
            }
        }
    }

    private MixinEntityLivingBase_MergeMobDrops(World worldIn) {
        super(worldIn);
    }
}
