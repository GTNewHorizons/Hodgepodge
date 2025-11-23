package com.mitchej123.hodgepodge.mixins.early.minecraft.debug;

import net.minecraft.client.renderer.texture.DynamicTexture;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mitchej123.hodgepodge.util.TexturesDebug;

@Mixin(DynamicTexture.class)
public class MixinDynamicTexture {

    @Inject(method = "<init>(II)V", at = @At("RETURN"))
    private void hodgepodge$debug(int width, int height, CallbackInfo ci) {
        if (width > 16 || height > 16) {
            TexturesDebug.logDynamicTexture(width, height);
        }
    }

}
