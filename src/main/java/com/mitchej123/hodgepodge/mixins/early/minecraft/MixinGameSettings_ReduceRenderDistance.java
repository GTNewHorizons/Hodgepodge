package com.mitchej123.hodgepodge.mixins.early.minecraft;

import static java.lang.Math.min;

import net.minecraft.client.settings.GameSettings;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(GameSettings.class)
public class MixinGameSettings_ReduceRenderDistance {

    @Redirect(
            method = "loadOptions",
            at = @At(value = "FIELD", target = "Lnet/minecraft/client/settings/GameSettings;renderDistanceChunks:I"))
    public void hodgepodge$fixOptifineChunkLoadingCrash(GameSettings instance, int value) {
        instance.renderDistanceChunks = min(16, value);
    }
}
