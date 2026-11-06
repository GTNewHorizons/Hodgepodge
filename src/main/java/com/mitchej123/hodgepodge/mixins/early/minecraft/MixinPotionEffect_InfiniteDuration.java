package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Treats a duration of -1 as infinite, like newer Minecraft versions: the effect never counts down or expires. */
@Mixin(PotionEffect.class)
public class MixinPotionEffect_InfiniteDuration {

    @Unique
    private static final int HODGEPODGE$INFINITE = -1;

    @Shadow
    private int potionID;
    @Shadow
    private int duration;
    @Shadow
    private int amplifier;

    @Unique
    private int hodgepodge$infiniteTicks;

    @Inject(method = "onUpdate", at = @At("HEAD"), cancellable = true)
    private void hodgepodge$tickInfinite(EntityLivingBase entity, CallbackInfoReturnable<Boolean> cir) {
        if (this.duration != HODGEPODGE$INFINITE) return;
        // Potion#isReady relies on a counting duration (regeneration, poison), so give it a tick counter instead.
        final Potion potion = Potion.potionTypes[this.potionID];
        if (potion.isReady(++this.hodgepodge$infiniteTicks, this.amplifier)) {
            potion.performEffect(entity, this.amplifier);
        }
        cir.setReturnValue(true);
    }

    @Inject(method = "combine", at = @At("HEAD"), cancellable = true)
    private void hodgepodge$combineInfinite(PotionEffect other, CallbackInfo ci) {
        // Vanilla keeps the longer duration, which would replace -1 with a finite one. An incoming -1 is ignored
        // while a finite effect runs, so a potion is not turned into a permanent effect that nothing would remove.
        if (other.getAmplifier() != this.amplifier) return;
        if (this.duration == HODGEPODGE$INFINITE || other.getDuration() == HODGEPODGE$INFINITE) {
            ci.cancel();
        }
    }
}
