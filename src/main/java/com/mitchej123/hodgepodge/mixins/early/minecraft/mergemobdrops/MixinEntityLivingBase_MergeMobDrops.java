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

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mitchej123.hodgepodge.util.MergeMobDropsHelper;
import com.mitchej123.hodgepodge.util.MobDropsContainerContext;

import cpw.mods.fml.common.Loader;

// Merge and dispatch mob drops. See TweaksConfig.mergeMobDrops and Mixins.MERGE_MOB_DROPS for more info
@Mixin(EntityLivingBase.class)
public abstract class MixinEntityLivingBase_MergeMobDrops extends Entity {

    @Inject(
            method = "onDeath",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityLivingBase;func_146066_aG()Z")) // canDropLoot()
    private void startMobDropsCollection(DamageSource cause, CallbackInfo ci) {
        MobDropsContainerContext.start(); // Start collecting mob drops
    }

    @WrapOperation(
            method = "onDeath",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraftforge/common/ForgeHooks;onLivingDrops(Lnet/minecraft/entity/EntityLivingBase;Lnet/minecraft/util/DamageSource;Ljava/util/ArrayList;IZI)Z",
                    remap = false))
    private boolean redirectLivingDrops(EntityLivingBase entity, DamageSource source,
            ArrayList<EntityItem> capturedDrops, int lootingLevel, boolean recentlyHit, int specialDropValue,
            Operation<Boolean> original) {

        List<ItemStack> drops = MobDropsContainerContext.end();

        /* ====== Compatibility patches start ===== */

        // Apply Battlegear2 bow luck enchantment equivalent
        if (Loader.isModLoaded("battlegear2")) {
            MergeMobDropsHelper.applyBowLuck(source, drops);
        }

        /* ====== Compatibility patches end ===== */

        MergeMobDropsHelper.mergeMobDrops(drops);

        // Convert to entities
        int len = drops.size();
        capturedDrops.ensureCapacity(capturedDrops.size() + len);
        for (int i = 0; i < len; i++) {
            ItemStack drop = drops.get(i);
            double offsetY = MobDropsContainerContext.getOffset(i);
            EntityItem entityItem = new EntityItem(this.worldObj, this.posX, this.posY + offsetY, this.posZ, drop);
            entityItem.delayBeforeCanPickup = 10;
            capturedDrops.add(entityItem);
        }
        drops.clear();
        MobDropsContainerContext.clearOffsets();

        return original.call(this, source, capturedDrops, lootingLevel, recentlyHit, specialDropValue);
    }

    private MixinEntityLivingBase_MergeMobDrops(World worldIn) {
        super(worldIn);
    }
}
