package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.mitchej123.hodgepodge.config.TweaksConfig;

/**
 * To be applied after {@link MixinEntityLivingBase_FixPotionException}
 */
@Mixin(value = EntityLivingBase.class, priority = 1001)
public class MixinEntityLivingBase_HidePotionParticles {

    @WrapWithCondition(
            at = @At(target = "Lnet/minecraft/world/World;spawnParticle(Ljava/lang/String;DDDDDD)V", value = "INVOKE"),
            method = "updatePotionEffects()V")
    private boolean hodgepodge$showParticles(World instance, String particleName, double x, double y, double z,
            double velocityX, double velocityY, double velocityZ) {
        return !TweaksConfig.hidePotionParticlesFromSelf || (Object) this != Minecraft.getMinecraft().thePlayer;
    }
}
