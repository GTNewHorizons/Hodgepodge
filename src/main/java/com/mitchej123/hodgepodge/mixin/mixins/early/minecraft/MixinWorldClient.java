package com.mitchej123.hodgepodge.mixin.mixins.early.minecraft;

import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.multiplayer.WorldClient;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(WorldClient.class)
public class MixinWorldClient {

    @Redirect(
            method = "spawnEntityInWorld",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/audio/SoundHandler;playSound(Lnet/minecraft/client/audio/ISound;)V"))
    private void hodgepodge$CancelSound(SoundHandler soundHandler, ISound isound) {
        // do nothing
    }

}
