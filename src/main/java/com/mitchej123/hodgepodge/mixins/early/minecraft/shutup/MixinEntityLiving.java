package com.mitchej123.hodgepodge.mixins.early.minecraft.shutup;

import net.minecraft.entity.EntityLiving;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.sugar.Local;

@Mixin(EntityLiving.class)
public class MixinEntityLiving {

    @Inject(
            method = "playLivingSound",
            at = @At(
                    value = "INVOKE_ASSIGN",
                    target = "Lnet/minecraft/entity/EntityLiving;getLivingSound()Ljava/lang/String;",
                    shift = At.Shift.AFTER),
            cancellable = true)
    private void hodgepodge$skipNoneSounds(CallbackInfo ci, @Local String s) {
        if ("none".equals(s) || "".equals(s)) ci.cancel();
    }
}
