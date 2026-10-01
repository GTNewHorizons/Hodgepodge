package com.mitchej123.hodgepodge.mixins.early.fml;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mitchej123.hodgepodge.core.shared.AsyncFmlFileLog;

import cpw.mods.fml.common.FMLCommonHandler;

@Mixin(value = FMLCommonHandler.class, remap = false)
public class MixinFMLCommonHandler_DrainAsyncLog {

    @Inject(
            method = "exitJava",
            at = { @At(value = "INVOKE", target = "Ljava/lang/Runtime;halt(I)V"), @At(
                    value = "INVOKE",
                    target = "Lcpw/mods/fml/common/asm/transformers/TerminalTransformer$ExitVisitor;runtimeHaltCalled(Ljava/lang/Runtime;I)V") })
    private void hodgepodge$drainAsyncLog(int exitCode, boolean hardExit, CallbackInfo ci) {
        AsyncFmlFileLog.stopAll();
    }
}
