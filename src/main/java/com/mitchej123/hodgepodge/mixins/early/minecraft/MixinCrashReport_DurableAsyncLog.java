package com.mitchej123.hodgepodge.mixins.early.minecraft;

import java.io.File;

import net.minecraft.crash.CrashReport;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.mitchej123.hodgepodge.core.shared.AsyncFmlFileLog;

@Mixin(CrashReport.class)
public class MixinCrashReport_DurableAsyncLog {

    @Inject(method = "saveToFile", at = @At("HEAD"))
    private void hodgepodge$enterDurableLog(File file, CallbackInfoReturnable<Boolean> cir) {
        AsyncFmlFileLog.enterDurableMode();
    }
}
