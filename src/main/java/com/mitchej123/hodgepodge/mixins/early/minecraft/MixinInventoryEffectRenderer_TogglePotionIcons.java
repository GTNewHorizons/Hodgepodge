package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.client.renderer.InventoryEffectRenderer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mitchej123.hodgepodge.config.TweaksConfig;

@Mixin(InventoryEffectRenderer.class)
public class MixinInventoryEffectRenderer_TogglePotionIcons {

    @Inject(method = "func_147044_g", at = @At("HEAD"), cancellable = true)
    private void hodgepodge$togglePotionEffectIcons(CallbackInfo ci) {
        if (!TweaksConfig.showInventoryEffectIcons) {
            ci.cancel();
        }
    }
}
