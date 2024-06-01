package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.SoundCategory;
import net.minecraft.client.audio.SoundManager;
import net.minecraft.client.audio.SoundPoolEntry;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SoundManager.class)
public class MixinSoundManager {

    @ModifyArg(
            method = "setSoundCategoryVolume",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/audio/SoundManager$SoundSystemStarterThread;setMasterVolume(F)V"),
            index = 0,
            remap = false)
    public float hodgepodge$modifySetMasterVolumeArg(float volume) {
        return (float) Math.pow(volume, 2);
    }

    @Inject(method = "getNormalizedVolume", at = @At("RETURN"), cancellable = true)
    private void hodgepodge$modifyCategoryVolume(ISound sound, SoundPoolEntry poolEntry, SoundCategory category,
            CallbackInfoReturnable<Float> ci) {
        float scaledVolume = (float) Math.pow(ci.getReturnValue(), 2);
        ci.setReturnValue(scaledVolume);
    }
}
