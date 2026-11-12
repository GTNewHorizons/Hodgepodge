package com.mitchej123.hodgepodge.mixins.early.minecraft.mergemobdrops;

import java.util.ArrayList;
import java.util.List;

<<<<<<< HEAD
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.CombatTracker;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
=======
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
>>>>>>> 788be5e9ed7467381286ae22d9b54ab0ed911d94

import com.mitchej123.hodgepodge.config.TweaksConfig;
import com.mitchej123.hodgepodge.util.MergeMobDropsCompatHandler;
import com.mitchej123.hodgepodge.util.MobDropsContainerContext;

import cpw.mods.fml.common.Loader;

<<<<<<< HEAD
@Mixin(EntityLivingBase.class)
public abstract class MixinEntityLivingBase_MergeMobDrops extends Entity {

    /**
     * @author nimuck
     * @reason Merge mob drops with the help of MixinEntityLiving_MergeMobDrops. Overwriting is to nuke the default
     *         LivingDeathEvent/LivingDropEvent pipeline that forge has injected into, and manually fire a new one. See
     *         TweaksConfig.mergeMobDrops for more info
     */
    @Overwrite
    public void onDeath(DamageSource cause) {

        // Fire Forge's LivingDeathEvent and do nothing if canceled
        if (ForgeHooks.onLivingDeath((EntityLivingBase) (Object) this, cause)) {
            return;
        }

        /* ====== Fully intact vanilla logic start ===== */

        Entity entity = cause.getEntity();
        EntityLivingBase entitylivingbase = this.func_94060_bK(); // getAttackingEntity()

        if (this.scoreValue >= 0 && entitylivingbase != null) {
            entitylivingbase.addToPlayerScore(this, this.scoreValue);
        }

        if (entity != null) {
            entity.onKillEntity((EntityLivingBase) (Object) this);
        }

        this.dead = true;
        this.func_110142_aN().func_94549_h(); // getCombatTracker().reset()

        /* ====== Fully intact vanilla logic end ===== */

        if (!this.worldObj.isRemote) {
            int lootingLevel = 0;

            if (entity instanceof EntityPlayer) {
                lootingLevel = EnchantmentHelper.getLootingModifier((EntityLivingBase) entity);
            }

            MobDropsContainerContext.start();

            boolean isRecentlyHit = this.recentlyHit > 0;
            int specialDropValue = this.rand.nextInt(200) - lootingLevel;
            // canDropLoot()
            if (this.func_146066_aG() && this.worldObj.getGameRules().getGameRuleBooleanValue("doMobLoot")) {

                this.dropFewItems(isRecentlyHit, lootingLevel);
                this.dropEquipment(isRecentlyHit, lootingLevel);

                if (isRecentlyHit) {
                    if (specialDropValue < 5) {
                        this.dropRareDrop(specialDropValue <= 0 ? 1 : 0);
                    }
                }
            }

            List<ItemStack> drops = MobDropsContainerContext.end();

            if (drops != null && !drops.isEmpty()) {

                /* ====== Compatibility patches start ===== */

                // Apply Battlegear2 bow luck enchantment equivalent
                MergeMobDropsCompatHandler.applyBowLuck(cause, drops);

                /* ====== Compatibility patches end ===== */

                mergeMobDrops(drops);

                // Convert to entities
                ArrayList<EntityItem> realDrops = new ArrayList<>(drops.size());
                for (ItemStack drop : drops) {
                    if (drop != null) {
                        EntityItem entityItem = new EntityItem(this.worldObj, this.posX, this.posY, this.posZ, drop);
                        entityItem.delayBeforeCanPickup = 10;
                        realDrops.add(entityItem);
                    }
                }
                drops.clear();

                // Fire Forge's LivingDropEvent and don't drop any items if canceled
                boolean eventCanceled = ForgeHooks.onLivingDrops(
                        (EntityLivingBase) (Object) this,
                        cause,
                        realDrops,
                        lootingLevel,
                        isRecentlyHit,
                        specialDropValue);

                // Normally Entity.entityDropItem dispatches them, but currently it's nuked by MixinEntity_MergeMobDrops
                if (!eventCanceled) {
                    for (EntityItem entityItem : realDrops) {
                        if (entityItem != null) {
                            this.worldObj.spawnEntityInWorld(entityItem);
                        }
                    }
                }
            }
        }
        this.worldObj.setEntityState(this, (byte) 3);
=======
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
>>>>>>> 788be5e9ed7467381286ae22d9b54ab0ed911d94
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

<<<<<<< HEAD
        // RESPECT_MAX_STACK_SIZE || HYBRID choosing it || HYBRID_AVARITIA choosing it
        if (mode == 0 || mode == 2 || mode == 3) {
            /*
             * Predefining the size of the array is to prevent memory reallocations it grows. Estimation logic:
             * threshold/64 (stackables) + 4 (equipments) + 2 (several rare drops)
             */
            List<ItemStack> fragmentedDrops = new ArrayList<>(threshold / 64 + 6);
=======
        // RESPECT_MAX_STACK_SIZE || HYBRID choosing it
        if (mode == 0 || mode == 2) {
            /*
             * 72 is to prevent memory reallocations as the array grows. Estimation logic: 4096/64 (stackables) + 4
             * (equipments) + 4 (several rare drops)
             */
            List<ItemStack> fragmentedDrops = new ArrayList<>(72);
>>>>>>> 788be5e9ed7467381286ae22d9b54ab0ed911d94

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

<<<<<<< HEAD
    @Shadow
    public abstract EntityLivingBase func_94060_bK();

    @Shadow
    protected int scoreValue;

    @Shadow
    private boolean dead;

    @Shadow
    public abstract CombatTracker func_110142_aN();

    @Shadow
    protected abstract boolean func_146066_aG();

    @Shadow
    protected abstract void dropFewItems(boolean recentlyHit, int lootingLevel);

    @Shadow
    protected abstract void dropEquipment(boolean recentlyHit, int lootingLevel);

    @Shadow
    protected abstract void dropRareDrop(int lootingModifier);

    @Shadow
    protected int recentlyHit;
=======
    private void dispatchMobDrops(List<ItemStack> drops) {
        for (ItemStack drop : drops) {
            if (drop != null) {
                EntityItem entityItem = new EntityItem(this.worldObj, this.posX, this.posY, this.posZ, drop);
                entityItem.delayBeforeCanPickup = 10;
                this.worldObj.spawnEntityInWorld(entityItem);
            }
        }
    }
>>>>>>> 788be5e9ed7467381286ae22d9b54ab0ed911d94

    private MixinEntityLivingBase_MergeMobDrops(World worldIn) {
        super(worldIn);
    }
}
