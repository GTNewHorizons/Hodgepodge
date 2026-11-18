package com.mitchej123.hodgepodge.mixins.early.minecraft;

import java.util.Arrays;

import net.minecraft.potion.Potion;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/** Mixin that expands the potion list to 256. */
@Mixin(Potion.class)
public class MixinPotion {

    /** Array of potion types, different from minecraft.potion.Potion because it is 256 elements long. */
    @Shadow
    @Final
    public static final Potion[] potionTypes = Arrays.copyOf(Potion.potionTypes, 256);
}
