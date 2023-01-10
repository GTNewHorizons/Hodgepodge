package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.client.settings.GameSettings;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameSettings.class)
public class MixinGameSettings {

    @Dynamic("Field is added by optifine's ASM")
    public int ofChunkLoading;

    @Inject(method = "loadOptions", at = @At("TAIL"))
    public void hodgepodge$forceChunkLoadingToDefault(CallbackInfo ci) {
        this.ofChunkLoading = 0;
    }

    @Inject(method = "setOptionValue", at = @At("HEAD"), cancellable = true)
    public void hodgepodge$fixOptifineChunkLoadingCrash(GameSettings.Options option, int p_74306_2_, CallbackInfo ci) {
        if (option != null && option.name().equals("CHUNK_LOADING")) {
            ci.cancel();
        }
    }
}
