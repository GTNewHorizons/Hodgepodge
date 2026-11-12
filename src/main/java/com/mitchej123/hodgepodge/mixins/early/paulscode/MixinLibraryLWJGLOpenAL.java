package com.mitchej123.hodgepodge.mixins.early.paulscode;

import org.lwjgl.LWJGLException;
import org.lwjgl.openal.AL;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.mitchej123.hodgepodge.client.sound.OutputDeviceSupport;
import com.mitchej123.hodgepodge.config.SoundConfig;

import paulscode.sound.libraries.LibraryLWJGLOpenAL;

@Mixin(value = LibraryLWJGLOpenAL.class, remap = false)
public abstract class MixinLibraryLWJGLOpenAL {

    @Redirect(method = "init()V", at = @At(value = "INVOKE", target = "Lorg/lwjgl/openal/AL;create()V"))
    private void hodgepodge$initDevice() throws LWJGLException {
        createConfiguredDevice();
    }

    @Redirect(method = "libraryCompatible()Z", at = @At(value = "INVOKE", target = "Lorg/lwjgl/openal/AL;create()V"))
    private static void hodgepodge$probeDevice() throws LWJGLException {
        createConfiguredDevice();
    }

    private static void createConfiguredDevice() throws LWJGLException {
        String selected = OutputDeviceSupport.resolveStartupDevice(SoundConfig.outputDevice);
        if (selected == null || selected.isEmpty()) {
            AL.create();
        } else {
            AL.create(selected, 44100, 60, false);
        }
    }
}
