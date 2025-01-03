package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.client.audio.SoundManager;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(SoundManager.class)
public class MixinSoundManager {

    @ModifyArg(
            method = "setSoundCategoryVolume",
            at = @At(
                    remap = false,
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/audio/SoundManager$SoundSystemStarterThread;setMasterVolume(F)V"),
            index = 0)
    public float hodgepodge$modifySetMasterVolumeArg(float volume) {
        return (float) Math.pow(volume, 2);
    }

    @ModifyArg(
            method = "setSoundCategoryVolume",
            at = @At(
                    remap = false,
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/audio/SoundManager$SoundSystemStarterThread;setVolume(Ljava/lang/String;F)V"),
            index = 1)
    private float hodgepodge$modifySetCategoryVolumeArg(float volume) {
        return (float) Math.pow(volume, 2);
    }
}
