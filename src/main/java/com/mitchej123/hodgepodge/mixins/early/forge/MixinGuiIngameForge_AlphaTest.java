package com.mitchej123.hodgepodge.mixins.early.forge;

import net.minecraftforge.client.GuiIngameForge;

import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * The XP and jump bars are drawn with blending off, so they rely on GL_ALPHA_TEST for transparent pixels. Enable it
 * first in case an earlier render (or the open GUI from the last frame) left it disabled.
 */
@Mixin(GuiIngameForge.class)
public abstract class MixinGuiIngameForge_AlphaTest {

    @Inject(method = { "renderExperience", "renderJumpBar" }, at = @At("HEAD"), remap = false)
    private void hodgepodge$enableAlphaTest(int width, int height, CallbackInfo ci) {
        GL11.glEnable(GL11.GL_ALPHA_TEST);
    }
}
