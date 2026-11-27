package com.mitchej123.hodgepodge.mixins.late.ztones;

import net.minecraftforge.client.event.MouseEvent;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.riciJak.Ztones.handler.EventHandler;

@Mixin(value = EventHandler.class, remap = false)
public class MixinEventHandler_DisableScrollSwap {

    @Inject(method = "onMouseEvent", at = @At("HEAD"), cancellable = true)
    private void hodgepodge$disableScrollSwap(MouseEvent event, CallbackInfo ci) {
        ci.cancel();
    }
}
