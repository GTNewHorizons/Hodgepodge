package com.mitchej123.hodgepodge.mixins.hooks;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;

import org.lwjgl.opengl.GL11;

public final class GLScissorHelper {

    public static void glScissorByGuiCoords(Minecraft mc, int left, int top, int width, int height) {
        ScaledResolution res = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
        double scaleW = mc.displayWidth / res.getScaledWidth_double();
        double scaleH = mc.displayHeight / res.getScaledHeight_double();

        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor(
                (int) (left * scaleW),
                (int) (mc.displayHeight - ((top + height) * scaleH)),
                (int) (width * scaleW),
                (int) (height * scaleH));
    }

    public static void endGlScissor() {
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }
}
