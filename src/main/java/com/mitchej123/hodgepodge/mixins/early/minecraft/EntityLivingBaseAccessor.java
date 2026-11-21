package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.PotionEffect;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(EntityLivingBase.class)
public interface EntityLivingBaseAccessor {

    @Invoker("onNewPotionEffect")
    void hodgepodge$onNewPotionEffect(PotionEffect effect);

    @Invoker("onFinishedPotionEffect")
    void hodgepodge$onFinishedPotionEffect(PotionEffect effect);
}
