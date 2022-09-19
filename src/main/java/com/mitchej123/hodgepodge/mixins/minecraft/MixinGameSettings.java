package com.mitchej123.hodgepodge.mixins.minecraft;

import net.minecraft.client.settings.GameSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameSettings.class)
public class MixinGameSettings {

    @Inject(method = "setOptionValue", at = @At("HEAD"), cancellable = true)
    public void hodgepodge$fixOptifineChunkLoadingCrash(GameSettings.Options option, int p_74306_2_, CallbackInfo ci) {
        if (option.name().equals("CHUNK_LOADING")) {
            ci.cancel();
        }
    }
}
