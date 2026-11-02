package com.mitchej123.hodgepodge.mixins.early.minecraft;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mitchej123.hodgepodge.client.sound.BootSoundReload;
import com.mitchej123.hodgepodge.client.sound.LibraryHodgepodgeOpenAL;

import paulscode.sound.Library;
import paulscode.sound.SoundSystem;
import paulscode.sound.SoundSystemConfig;

@Mixin(targets = "net.minecraft.client.audio.SoundManager$SoundSystemStarterThread")
public abstract class MixinSoundSystemStarterThread_BootReload extends SoundSystem implements BootSoundReload.Engine {

    @Inject(method = "<init>*", at = @At("RETURN"), remap = false)
    private void hodgepodge$track(CallbackInfo ci) {
        BootSoundReload.onEngineConstructed(this);
    }

    @Override
    public String hodgepodge$restartReason() {
        synchronized (SoundSystemConfig.THREAD_SYNC) {
            final Library library = soundLibrary;
            if (!(library instanceof LibraryHodgepodgeOpenAL ours)) {
                return "sound library is " + (library == null ? "not initialized" : library.getClass().getName());
            }
            final Map<?, ?> sources = ours.getSources();
            if (sources != null && !sources.isEmpty()) return "sources already created";
            if (ours.hasDecodedBuffers()) return "sound buffers already decoded";
            return null;
        }
    }
}
