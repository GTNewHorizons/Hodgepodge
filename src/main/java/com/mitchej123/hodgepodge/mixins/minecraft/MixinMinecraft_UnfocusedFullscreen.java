package com.mitchej123.hodgepodge.mixins.minecraft;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Minecraft.class)
public class MixinMinecraft_UnfocusedFullscreen {
    @Redirect(
            method = "runGameLoop",
            at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/Display;isActive()Z"),
            remap = false)
    public boolean hodgepodge$fixUnfocusedFullscreen() {
        return true;
    }
}
