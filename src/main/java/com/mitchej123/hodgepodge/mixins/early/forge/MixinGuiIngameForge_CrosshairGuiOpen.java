package com.mitchej123.hodgepodge.mixins.early.forge;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiIngame;
import net.minecraftforge.client.GuiIngameForge;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiIngameForge.class)
public class MixinGuiIngameForge_CrosshairGuiOpen extends GuiIngame {

    @Inject(method = "renderCrosshairs", at = @At("HEAD"), cancellable = true, remap = false)
    public void hodgepodge$hideCrosshairInGui(int width, int height, CallbackInfo ci) {
        if (mc.currentScreen != null && !(mc.currentScreen instanceof GuiChat)) {
            ci.cancel();
        }
    }

    private MixinGuiIngameForge_CrosshairGuiOpen(Minecraft mc) {
        super(mc);
    }
}
