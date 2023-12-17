package com.mitchej123.hodgepodge.mixins.early.minecraft;

import java.net.HttpURLConnection;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.mitchej123.hodgepodge.Tags;

@Mixin(targets = "net/minecraft/client/renderer/ThreadDownloadImageData$1")
public class MixinThreadDownloadImageData {

    @SuppressWarnings("UnresolvedMixinReference") // mcdev cannot find references in anonymous classes correctly
    @Inject(
            method = "run()V",
            at = @At(value = "INVOKE", target = "Ljava/net/HttpURLConnection;setDoInput(Z)V"),
            locals = LocalCapture.CAPTURE_FAILSOFT,
            remap = false)
    private void hodgepodge$injectRun(CallbackInfo ci, HttpURLConnection httpURLConnection) {
        httpURLConnection.setRequestProperty(
                "User-Agent",
                "Minecraft/1.7.10 Hodgepodge/" + Tags.VERSION + " (+https://github.com/GTNewHorizons/Hodgepodge)");
    }
}
