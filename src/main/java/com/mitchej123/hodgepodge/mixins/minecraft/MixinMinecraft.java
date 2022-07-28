package com.mitchej123.hodgepodge.mixins.minecraft;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Util;
import org.lwjgl.opengl.Display;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MixinMinecraft {

    @Shadow
    private boolean fullscreen;

    /*
     *  LWJGL bug https://bugs.mojang.com/browse/MC-68754?page=com.atlassian.jira.plugin.system.issuetabpanels%3Acomment-tabpanel&showAll=true
     */
    @Inject(method = "toggleFullscreen", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/Display;setVSyncEnabled(Z)V"), remap = false)
    private void toggleResizable(CallbackInfo ci) {
        if (!this.fullscreen && (Util.getOSType() == Util.EnumOS.WINDOWS)) {
            Display.setResizable(false);
            Display.setResizable(true);
        }
    }
}
