package com.mitchej123.hodgepodge.mixins.late.ztones;

import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;

import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.riciJak.Ztones.handler.EventHandler;

import cpw.mods.fml.client.registry.ClientRegistry;

@Mixin(value = EventHandler.class, remap = false)
public class MixinEventHandler_CycleKey {

    @Unique
    private static final KeyBinding hodgepodge$cycleKey = new KeyBinding(
            "key.ztonesCycle.desc",
            Keyboard.KEY_LCONTROL,
            "ZTones");

    @Inject(method = "<init>", at = @At("RETURN"))
    private void hodgepodge$registerCycleKey(CallbackInfo ci) {
        ClientRegistry.registerKeyBinding(hodgepodge$cycleKey);
    }

    @Redirect(method = "onMouseEvent", at = @At(value = "INVOKE", target = "Lorg/lwjgl/input/Keyboard;isKeyDown(I)Z"))
    private boolean hodgepodge$checkCycleKey(int key) {
        return GameSettings.isKeyDown(hodgepodge$cycleKey);
    }
}
