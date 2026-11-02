package com.mitchej123.hodgepodge.mixins.early.forge;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiIngame;
import net.minecraftforge.client.GuiIngameForge;

import org.spongepowered.asm.mixin.Mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;

@Mixin(GuiIngameForge.class)
public class MixinGuiIngameForge_CrosshairGuiOpen extends GuiIngame {

    @WrapMethod(method = "renderCrosshairs", remap = false)
    public void hodgepodge$hideCrosshairInGui(int width, int height, Operation<Void> original) {
        if (mc.currentScreen == null || mc.currentScreen instanceof GuiChat) {
            original.call(width, height);
        } else {
            // Preserve the texture bind that Forge's boss bar relies on.
            mc.getTextureManager().bindTexture(icons);
        }
    }

    private MixinGuiIngameForge_CrosshairGuiOpen(Minecraft mc) {
        super(mc);
    }
}
