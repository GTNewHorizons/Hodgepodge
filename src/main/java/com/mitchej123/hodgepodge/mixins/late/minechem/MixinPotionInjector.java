package com.mitchej123.hodgepodge.mixins.late.minechem;

import net.minecraft.potion.Potion;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mitchej123.hodgepodge.config.TweaksConfig;

import minechem.potion.PotionInjector;
import minechem.potion.PotionProvider;

@Mixin(value = PotionInjector.class, remap = false)
public class MixinPotionInjector {

    @Shadow
    public static Potion atropineHigh;

    @Inject(method = "inject", at = @At("HEAD"), cancellable = true)
    private static void hodgepodge$inject(CallbackInfo ci) {
        if (Potion.potionTypes.length > 255) {
            ci.cancel();
        }
        atropineHigh = new PotionProvider(TweaksConfig.atropineHighID, true, 0x00FF6E).setPotionName("Delirium");
    }
}
