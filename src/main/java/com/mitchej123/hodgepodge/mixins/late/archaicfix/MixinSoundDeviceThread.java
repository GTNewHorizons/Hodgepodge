package com.mitchej123.hodgepodge.mixins.late.archaicfix;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.mitchej123.hodgepodge.client.sound.OutputDeviceSupport;

@Pseudo
@Mixin(targets = "org.embeddedt.archaicfix.helpers.SoundDeviceThread", remap = false)
public class MixinSoundDeviceThread {

    @Redirect(method = "run", at = @At(value = "INVOKE", target = "Ljava/lang/Thread;interrupted()Z"))
    private boolean hodgepodge$stopWhenOutputDeviceSupportIsActive() {
        return Thread.interrupted() || OutputDeviceSupport.takesOwnership();
    }
}
