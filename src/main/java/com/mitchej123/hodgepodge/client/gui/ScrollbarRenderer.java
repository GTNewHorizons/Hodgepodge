package com.mitchej123.hodgepodge.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import com.mitchej123.hodgepodge.Hodgepodge;

/**
 * Draws a texture-based scrollbar track/thumb using {@code scrollbar.png} (12x6), a 6px-wide Track column followed by a
 * 6px-wide Thumb column, each sliced vertically into a 2px top cap, a 2px bottom cap, and a repeatable 2px middle band
 * that is tiled to fill the requested height.
 */
public class ScrollbarRenderer {

    private static final ResourceLocation TEXTURE = new ResourceLocation(
            Hodgepodge.MODID,
            "textures/gui/scrollbar.png");

    private static final int TEXTURE_WIDTH = 12;
    private static final int TEXTURE_HEIGHT = 6;
    private static final int TRACK_WIDTH = 6;
    private static final int SLICE_SIZE = 2;

    private static final int TRACK_U_OFFSET = 0;
    private static final int THUMB_U_OFFSET = TRACK_WIDTH;

    public static void drawTrack(int x, int y, int height) {
        draw(x, y, height, TRACK_U_OFFSET);
    }

    public static void drawThumb(int x, int y, int height) {
        draw(x, y, height, THUMB_U_OFFSET);
    }

    private static void draw(int x, int y, int height, int u) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(TEXTURE);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        int capSize = Math.min(SLICE_SIZE, height / 2);
        drawSlice(x, y, u, 0, capSize);

        int middleY = y + capSize;
        int bottomCapY = y + height - capSize;
        int remaining = bottomCapY - middleY;

        while (remaining > 0) {
            int sliceHeight = Math.min(SLICE_SIZE, remaining);
            drawSlice(x, middleY, u, SLICE_SIZE, sliceHeight);
            middleY += sliceHeight;
            remaining -= sliceHeight;
        }

        drawSlice(x, bottomCapY, u, TEXTURE_HEIGHT - capSize, capSize);

        GL11.glDisable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    }

    private static void drawSlice(int x, int y, int u, int v, int height) {
        if (height <= 0) {
            return;
        }

        float uMin = u / (float) TEXTURE_WIDTH;
        float uMax = (u + TRACK_WIDTH) / (float) TEXTURE_WIDTH;
        float vMin = v / (float) TEXTURE_HEIGHT;
        float vMax = (v + height) / (float) TEXTURE_HEIGHT;

        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(x, y + height, 0.0D, uMin, vMax);
        tessellator.addVertexWithUV(x + TRACK_WIDTH, y + height, 0.0D, uMax, vMax);
        tessellator.addVertexWithUV(x + TRACK_WIDTH, y, 0.0D, uMax, vMin);
        tessellator.addVertexWithUV(x, y, 0.0D, uMin, vMin);
        tessellator.draw();
    }
}
