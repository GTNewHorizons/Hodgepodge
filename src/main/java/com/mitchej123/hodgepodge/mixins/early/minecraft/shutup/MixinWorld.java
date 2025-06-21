package com.mitchej123.hodgepodge.mixins.early.minecraft.shutup;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Makes World#playSoundAtEntity ignore sound names that are equal to "none" and "".
 * 
 * @author ah-OOG-ah, Charsy89
 * @reason Minecraft complains in the log that "none" and "" are unknown soundEvents, instead of ignoring them. This can
 *         get spammy, especially with mobs that often return these soundEvents.
 */
@Mixin(World.class)
public class MixinWorld {

    @Inject(method = "playSoundAtEntity", at = @At(value = "HEAD"), cancellable = true)
    private void hodgepodge$skipEmptySounds(Entity e, String soundName, float vol, float pitch, CallbackInfo ci) {
        if ("none".equals(soundName) || "".equals(soundName)) ci.cancel();
    }
}
