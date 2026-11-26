package com.mitchej123.hodgepodge.mixins.early.minecraft.mergemobdrops;

import java.util.ArrayList;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mitchej123.hodgepodge.mixins.interfaces.IMobDropsContextHolder;
import com.mitchej123.hodgepodge.util.MergeDropsHelper;
import com.mitchej123.hodgepodge.util.MobDropsContainerContext;

import cpw.mods.fml.common.Loader;

// Merge and dispatch mob drops. See TweaksConfig.mergeMobDrops and Mixins.MERGE_MOB_DROPS for more info
@Mixin(EntityLivingBase.class)
public abstract class MixinEntityLivingBase_MergeMobDrops extends Entity implements IMobDropsContextHolder {

    @Unique
    private MobDropsContainerContext hodgepodge$ctx = null;

    @Override
    public MobDropsContainerContext hodgepodge$getDropsContext() {
        return this.hodgepodge$ctx;
    }

    @Inject(
            method = "onDeath",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityLivingBase;func_146066_aG()Z")) // canDropLoot()
    private void startCollectingMobDrops(DamageSource cause, CallbackInfo ci) {
        this.hodgepodge$ctx = new MobDropsContainerContext(); // Start collecting mob drops
    }

    @WrapOperation(
            method = "onDeath",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraftforge/common/ForgeHooks;onLivingDrops(Lnet/minecraft/entity/EntityLivingBase;Lnet/minecraft/util/DamageSource;Ljava/util/ArrayList;IZI)Z",
                    remap = false))
    private boolean redirectedOnLivingDrops(EntityLivingBase entity, DamageSource source,
            ArrayList<EntityItem> capturedDrops, int lootingLevel, boolean recentlyHit, int specialDropValue,
            Operation<Boolean> original) {
        MobDropsContainerContext ctx = this.hodgepodge$ctx;

        /* ====== Compatibility patches start ===== */

        // Apply Battlegear2 bow luck enchantment equivalent
        if (Loader.isModLoaded("battlegear2")) {
            MergeDropsHelper.applyBowLuck(source, ctx.drops);
        }

        /* ====== Compatibility patches end ===== */

        MergeDropsHelper.mergeMobDrops(ctx);

        // Convert to entities
        int len = ctx.drops.size();
        capturedDrops.ensureCapacity(capturedDrops.size() + len);
        ctx.ensureOffsetCapacity(len);
        for (int i = 0; i < len; i++) {
            ItemStack drop = ctx.drops.get(i);
            EntityItem entityItem = new EntityItem(
                    this.worldObj,
                    this.posX,
                    this.posY + ctx.offsets[i],
                    this.posZ,
                    drop);
            entityItem.delayBeforeCanPickup = 10;
            capturedDrops.add(entityItem);
        }

        return original.call(this, source, capturedDrops, lootingLevel, recentlyHit, specialDropValue);
    }

    private MixinEntityLivingBase_MergeMobDrops(World worldIn) {
        super(worldIn);
    }
}
