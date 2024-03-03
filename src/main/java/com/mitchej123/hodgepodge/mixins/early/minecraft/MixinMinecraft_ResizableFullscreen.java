package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Util;

import org.lwjgl.opengl.Display;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MixinMinecraft_ResizableFullscreen {

    @Shadow
    private boolean fullscreen;

    /**
     * LWJGL bug
     * 
     * @see <a href=https://bugs.mojang.com/browse/MC-68754>MC-68754</a>
     */
    @Inject(
            method = "toggleFullscreen",
            at = @At(
                    opcode = Opcodes.GETFIELD,
                    target = "Lnet/minecraft/client/Minecraft;gameSettings:Lnet/minecraft/client/settings/GameSettings;",
                    value = "FIELD"))
    private void hodgepodge$fixFullscreenResizable(CallbackInfo ci) {
        if (!this.fullscreen && (Util.getOSType() == Util.EnumOS.WINDOWS)) {
            Display.setResizable(false);
            Display.setResizable(true);
        }
    }
}
