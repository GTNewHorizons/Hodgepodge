package com.mitchej123.hodgepodge.mixins.late.battlegear2;

import net.minecraftforge.event.entity.living.LivingDropsEvent;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import mods.battlegear2.BattlemodeHookContainerClass;

/*
 * If TweaksConfig.mergeMobDrops = true, cancel the original bow luck logic and restore it using the
 * MergeMobDropsCompatHandler.applyBowLuck for compat
 */
@Mixin(BattlemodeHookContainerClass.class)
public abstract class MixinBattlemodeHookContainerClass {

    @Inject(method = "onDrop", at = @At("HEAD"), cancellable = true, remap = false)
    private void hodgepodge$cancelBattlegearDrop(LivingDropsEvent event, CallbackInfo ci) {
        ci.cancel();
    }
}
