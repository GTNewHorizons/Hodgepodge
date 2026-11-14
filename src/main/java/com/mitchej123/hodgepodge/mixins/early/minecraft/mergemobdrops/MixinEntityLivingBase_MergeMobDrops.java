package com.mitchej123.hodgepodge.mixins.early.minecraft.mergemobdrops;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mitchej123.hodgepodge.util.MobDropsContainerContext;

@Mixin(EntityLivingBase.class)
public abstract class MixinEntityLivingBase_MergeMobDrops {

    /**
     * @author nimuck
     * @reason Start collecting drops from mobs as ItemStack. See TweaksConfig.mergeMobDrops and Mixins.MERGE_MOB_DROPS
     *         for more info
     */
    @Inject(
            method = "onDeath",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityLivingBase;func_146066_aG()Z")) // canDropLoot()
    private void hodgepodge$startMobDropsCollection(DamageSource cause, CallbackInfo ci) {
        MobDropsContainerContext.start();
    }
}
