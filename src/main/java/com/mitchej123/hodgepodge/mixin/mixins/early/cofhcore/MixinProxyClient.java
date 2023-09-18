package com.mitchej123.hodgepodge.mixin.mixins.early.cofhcore;

import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import cofh.core.ProxyClient;

@Mixin(ProxyClient.class)
public class MixinProxyClient {

    @ModifyConstant(method = "<clinit>", constant = { @Constant(intValue = 46), @Constant(intValue = 47) })
    private static int hodgepodge$UnbindKeybind(int original) {
        return Keyboard.KEY_NONE;
    }

}
