package com.mitchej123.hodgepodge.mixins.early.cofhcore;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import cofh.mod.updater.UpdateCheckThread;

@Mixin(UpdateCheckThread.class)
public class MixinCoFHCoreUpdateCheck {

    /**
     * @author mitchej123
     * @reason Update URL is long since gone
     */
    @Inject(at = @At("HEAD"), cancellable = true, method = "run", remap = false)
    private void hodgepodge$exit(CallbackInfo ci) {
        ci.cancel();
    }

}
