package com.mitchej123.hodgepodge.mixins.early.minecraft.network;

import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mitchej123.hodgepodge.config.TweaksConfig;
import com.mitchej123.hodgepodge.util.InfinitePotionDuration;

@Mixin(NetHandlerPlayClient.class)
public class MixinNetHandlerPlayClient_InfinitePotionDuration {

    @WrapOperation(
            method = "handleEntityEffect",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/EntityLivingBase;addPotionEffect(Lnet/minecraft/potion/PotionEffect;)V"))
    private void hodgepodge$applyInfiniteEffect(EntityLivingBase entity, PotionEffect incoming,
            Operation<Void> original) {
        if (!TweaksConfig.infinitePotionDuration && incoming.getDuration() == InfinitePotionDuration.INFINITE) {
            int id = incoming.getPotionID();
            if (id >= 0 && id < Potion.potionTypes.length && Potion.potionTypes[id] != null) {
                PotionEffect current = entity.getActivePotionEffect(Potion.potionTypes[id]);
                if (current != null && current.getDuration() != InfinitePotionDuration.INFINITE
                        && current.getAmplifier() == incoming.getAmplifier()
                        && entity.isPotionApplicable(incoming)) {
                    entity.removePotionEffectClient(id);
                }
            }
        }
        original.call(entity, incoming);
    }
}
