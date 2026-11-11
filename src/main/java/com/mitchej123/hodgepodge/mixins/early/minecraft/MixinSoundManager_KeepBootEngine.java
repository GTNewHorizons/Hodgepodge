package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.client.audio.SoundManager;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.mitchej123.hodgepodge.client.sound.BootSoundReload;

@Mixin(SoundManager.class)
public class MixinSoundManager_KeepBootEngine {

    @Shadow
    private boolean loaded;

    @WrapWithCondition(
            method = "reloadSoundSystem",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/audio/SoundManager;unloadSoundSystem()V"))
    private boolean hodgepodge$keepBootEngine(SoundManager self) {
        if (!BootSoundReload.shouldKeepEngine(loaded)) return true;
        self.stopAllSounds();
        return false;
    }

    @Inject(
            method = "loadSoundSystem",
            at = @At(value = "INVOKE", target = "Ljava/lang/Thread;start()V", remap = false))
    private void hodgepodge$engineStarting(CallbackInfo ci) {
        BootSoundReload.onEngineStarting();
    }

    @Inject(
            method = "playSound",
            at = { @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/audio/SoundManager$SoundSystemStarterThread;newStreamingSource(ZLjava/lang/String;Ljava/net/URL;Ljava/lang/String;ZFFFIF)V",
                    remap = false),
                    @At(
                            value = "INVOKE",
                            target = "Lnet/minecraft/client/audio/SoundManager$SoundSystemStarterThread;newSource(ZLjava/lang/String;Ljava/net/URL;Ljava/lang/String;ZFFFIF)V",
                            remap = false) })
    private void hodgepodge$soundCreated(CallbackInfo ci) {
        BootSoundReload.onSoundCreated();
    }
}
