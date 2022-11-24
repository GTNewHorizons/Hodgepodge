package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.client.audio.SoundCategory;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.audio.SoundList;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(value = SoundHandler.class)
public class MixinSoundHandler {

    @Inject(method = "loadSoundResource", at = @At("HEAD"), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    private void loadSoundResource(ResourceLocation resourceLocation, SoundList soundList, CallbackInfo callbackInfo) {
        String name = resourceLocation.toString();

        if (name.startsWith("minecraft:step")
                || name.startsWith("minecraft:random.bow")
                || name.equals("minecraft:game.potion.smash")) {
            soundList.setSoundCategory(SoundCategory.PLAYERS);
        }
    }
}
