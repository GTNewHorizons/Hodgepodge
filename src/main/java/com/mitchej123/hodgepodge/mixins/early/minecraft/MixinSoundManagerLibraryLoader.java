package com.mitchej123.hodgepodge.mixins.early.minecraft;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(targets = "net.minecraft.client.audio.SoundManager$1")
public class MixinSoundManagerLibraryLoader {

    @SuppressWarnings("UnresolvedMixinReference")
    @ModifyArg(
            method = "run()V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/audio/SoundManager$SoundSystemStarterThread;setMasterVolume(F)V"),
            index = 0,
            remap = false)
    private float hodgepodge$modifySetMasterVolumeArg(float volume) {
        return (float) Math.pow(volume, 2);
    }
}
