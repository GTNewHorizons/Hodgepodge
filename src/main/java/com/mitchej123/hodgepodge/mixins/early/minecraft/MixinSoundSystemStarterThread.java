package com.mitchej123.hodgepodge.mixins.early.minecraft;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import paulscode.sound.SoundSystem;

@Mixin(targets = "net/minecraft/client/audio/SoundManager$SoundSystemStarterThread")
public class MixinSoundSystemStarterThread extends SoundSystem {

    @SuppressWarnings("UnresolvedMixinReference")
    @Inject(
            method = "playing(Ljava/lang/String;)Z",
            at = @At(value = "INVOKE", target = "Lpaulscode/sound/Library;getSources()Ljava/util/HashMap;"),
            remap = false,
            cancellable = true)
    private void playingPatch(CallbackInfoReturnable<Boolean> cir) {
        if (this.soundLibrary.getSources() == null) cir.setReturnValue(false);
    }
}
