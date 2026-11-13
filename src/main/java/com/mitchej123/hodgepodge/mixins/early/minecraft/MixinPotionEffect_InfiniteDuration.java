package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.mitchej123.hodgepodge.mixins.interfaces.PotionEffectHiddenExt;
import com.mitchej123.hodgepodge.util.InfinitePotionDuration;

/**
 * Treats a duration of -1 as infinite, like newer Minecraft versions: the effect never counts down or expires, wins
 * over a weaker or equal effect, and keeps a weaker effect hidden until it ends.
 */
// Higher priority than the other PotionEffect mixins, so the NBT injectors run after their overwrites.
@Mixin(value = PotionEffect.class, priority = 1500)
public class MixinPotionEffect_InfiniteDuration implements PotionEffectHiddenExt {

    @Shadow
    private int potionID;
    @Shadow
    private int duration;
    @Shadow
    private int amplifier;
    @Shadow
    private boolean isAmbient;

    @Unique
    private PotionEffect hodgepodge$hidden;
    @Unique
    private int hodgepodge$infiniteTicks;

    @Override
    public PotionEffect hodgepodge$getHidden() {
        return this.hodgepodge$hidden;
    }

    @Override
    public void hodgepodge$setHidden(PotionEffect hidden) {
        this.hodgepodge$hidden = hidden;
    }

    @Unique
    private static boolean hodgepodge$isShorter(int duration, int other) {
        return duration != InfinitePotionDuration.INFINITE
                && (other == InfinitePotionDuration.INFINITE || duration < other);
    }

    @Unique
    private static PotionEffect hodgepodge$copyOf(PotionEffect effect) {
        return new PotionEffect(
                effect.getPotionID(),
                effect.getDuration(),
                effect.getAmplifier(),
                effect.getIsAmbient());
    }

    @Inject(method = "onUpdate", at = @At("HEAD"), cancellable = true)
    private void hodgepodge$tickInfinite(EntityLivingBase entity, CallbackInfoReturnable<Boolean> cir) {
        if (this.duration != InfinitePotionDuration.INFINITE) return;
        // Potion#isReady relies on a counting duration (regeneration, poison), so give it a tick counter instead.
        // A modded potion that reads the real remaining duration there sees this counter, restarting at 1.
        final Potion potion = Potion.potionTypes[this.potionID];
        if (potion.isReady(++this.hodgepodge$infiniteTicks, this.amplifier)) {
            potion.performEffect(entity, this.amplifier);
        }
        cir.setReturnValue(true);
    }

    @Inject(method = "onUpdate", at = @At("RETURN"), cancellable = true)
    private void hodgepodge$promoteHidden(EntityLivingBase entity, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue() || this.hodgepodge$hidden == null || entity.worldObj.isRemote) return;
        // Finish and re-add the effect, so attribute modifiers and the client copy follow the hidden effect.
        final EntityLivingBaseAccessor accessor = (EntityLivingBaseAccessor) entity;
        final PotionEffect self = (PotionEffect) (Object) this;
        accessor.hodgepodge$onFinishedPotionEffect(self);
        final PotionEffect next = this.hodgepodge$hidden;
        this.hodgepodge$hidden = ((PotionEffectHiddenExt) next).hodgepodge$getHidden();
        this.amplifier = next.getAmplifier();
        this.duration = next.getDuration();
        this.isAmbient = next.getIsAmbient();
        this.hodgepodge$infiniteTicks = 0;
        accessor.hodgepodge$onNewPotionEffect(self);
        cir.setReturnValue(true);
    }

    @Inject(method = "combine", at = @At("HEAD"), cancellable = true)
    private void hodgepodge$combineInfinite(PotionEffect other, CallbackInfo ci) {
        final int otherDuration = other.getDuration();
        if (this.duration != InfinitePotionDuration.INFINITE && otherDuration != InfinitePotionDuration.INFINITE) {
            return;
        }
        ci.cancel();
        // Same rules as newer versions: a stronger effect replaces this one, and the weaker one stays hidden.
        boolean changed = false;
        if (other.getAmplifier() > this.amplifier) {
            if (hodgepodge$isShorter(otherDuration, this.duration)) {
                final PotionEffect saved = hodgepodge$copyOf((PotionEffect) (Object) this);
                ((PotionEffectHiddenExt) saved).hodgepodge$setHidden(this.hodgepodge$hidden);
                this.hodgepodge$hidden = saved;
            }
            this.amplifier = other.getAmplifier();
            this.duration = otherDuration;
            changed = true;
        } else if (hodgepodge$isShorter(this.duration, otherDuration)) {
            if (other.getAmplifier() == this.amplifier) {
                this.duration = otherDuration;
                changed = true;
            } else if (this.hodgepodge$hidden == null) {
                this.hodgepodge$hidden = hodgepodge$copyOf(other);
            } else {
                this.hodgepodge$hidden.combine(other);
            }
        }
        // An infinite effect keeps its ambient flag against a finite one, so whoever applied it can still remove it.
        if (changed || (!other.getIsAmbient() && this.isAmbient && this.duration != InfinitePotionDuration.INFINITE)) {
            this.isAmbient = other.getIsAmbient();
        }
    }

    @Inject(method = "writeCustomPotionEffectToNBT", at = @At("RETURN"))
    private void hodgepodge$writeHidden(NBTTagCompound tag, CallbackInfoReturnable<NBTTagCompound> cir) {
        if (this.hodgepodge$hidden != null) {
            tag.setTag("HiddenEffect", this.hodgepodge$hidden.writeCustomPotionEffectToNBT(new NBTTagCompound()));
        }
    }

    @Inject(method = "readCustomPotionEffectFromNBT", at = @At("RETURN"))
    private static void hodgepodge$readHidden(NBTTagCompound tag, CallbackInfoReturnable<PotionEffect> cir) {
        final PotionEffect effect = cir.getReturnValue();
        if (effect != null && tag.hasKey("HiddenEffect", 10)) {
            ((PotionEffectHiddenExt) effect).hodgepodge$setHidden(
                    PotionEffect.readCustomPotionEffectFromNBT(tag.getCompoundTag("HiddenEffect")));
        }
    }
}
