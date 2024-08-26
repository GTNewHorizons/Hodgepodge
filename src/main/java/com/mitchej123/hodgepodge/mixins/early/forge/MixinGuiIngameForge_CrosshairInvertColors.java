package com.mitchej123.hodgepodge.mixins.early.forge;

import net.minecraftforge.client.GuiIngameForge;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;

@Mixin(GuiIngameForge.class)
public class MixinGuiIngameForge_CrosshairInvertColors {

    @WrapWithCondition(
            method = "renderCrosshairs",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/OpenGlHelper;glBlendFunc(IIII)V"))
    private boolean hodgepodge$dontInvertColors(int i1, int i2, int i3, int i4) {
        return false;
    }

}
