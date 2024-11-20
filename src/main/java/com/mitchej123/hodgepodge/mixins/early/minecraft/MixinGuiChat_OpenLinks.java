package com.mitchej123.hodgepodge.mixins.early.minecraft;

import java.net.URI;

import net.minecraft.client.gui.GuiChat;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.gtnewhorizon.gtnhlib.util.FilesUtil;

@Mixin(GuiChat.class)
public class MixinGuiChat_OpenLinks {

    // @reason The Vanilla method doesn't work on some OS
    @Inject(method = "func_146407_a", at = @At("HEAD"), cancellable = true)
    private void hodgepodge$fixFileOpening(URI uri, CallbackInfo ci) {
        FilesUtil.openUri(uri);
        ci.cancel();
    }

}
