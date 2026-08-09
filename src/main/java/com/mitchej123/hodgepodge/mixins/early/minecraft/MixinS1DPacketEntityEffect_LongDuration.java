package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.network.play.server.S1DPacketEntityEffect;
import net.minecraft.potion.PotionEffect;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mitchej123.hodgepodge.mixins.interfaces.S1DPacketEntityEffectExt;
import com.mitchej123.hodgepodge.net.MessagePotionDuration;

@Mixin(S1DPacketEntityEffect.class)
public class MixinS1DPacketEntityEffect_LongDuration implements S1DPacketEntityEffectExt {

    @Unique
    private MessagePotionDuration hodgepodge$durationMessage;

    @Inject(method = "<init>(ILnet/minecraft/potion/PotionEffect;)V", at = @At("RETURN"))
    private void hodgepodge$keepRealDuration(int entityId, PotionEffect effect, CallbackInfo ci) {
        if (effect.getDuration() > Short.MAX_VALUE) {
            this.hodgepodge$durationMessage = new MessagePotionDuration(
                    entityId,
                    effect.getPotionID(),
                    effect.getDuration());
        }
    }

    @Override
    public MessagePotionDuration hodgepodge$getDurationMessage() {
        return this.hodgepodge$durationMessage;
    }
}
