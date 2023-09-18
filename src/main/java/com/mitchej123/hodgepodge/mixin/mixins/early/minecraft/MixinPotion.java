package com.mitchej123.hodgepodge.mixin.mixins.early.minecraft;

import java.util.Arrays;

import net.minecraft.potion.Potion;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Potion.class)
public class MixinPotion {

    @Shadow
    public static final Potion[] potionTypes = Arrays.copyOf(Potion.potionTypes, 256);
}
