package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.potion.PotionEffect;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(PotionEffect.class)
public interface PotionEffectAccessor {

    @Accessor("duration")
    void setDuration(int duration);
}
