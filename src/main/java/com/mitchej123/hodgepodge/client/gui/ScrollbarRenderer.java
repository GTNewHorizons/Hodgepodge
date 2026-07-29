package com.mitchej123.hodgepodge.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import com.mitchej123.hodgepodge.Hodgepodge;

/**
 * Draws a scrollbar from scrollbar.png, a 12x6 sheet with a 6px wide track column beside a 6px wide thumb column. Each
 * column splits into a 2px top cap, a 2px middle band tiled to fill the requested height, and a 2px bottom cap.
 *
 * <pre>
 *       u=0    u=6   u=12
 *  v=0  +------+------+
 *       |      |      |  top cap
 *  v=2  +------+------+
 *       |      |      |  middle band, tiled
 *  v=4  +------+------+
 *       |      |      |  bottom cap
 *  v=6  +------+------+
 *        track  thumb
 * </pre>
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

    public static void drawScrollbar(int scrollBarX, int top, int trackHeight, int contentHeight, float scrollAmount,
            int scrollRange) {
        if (scrollRange <= 0) {
            return;
        }

        int thumbHeight = trackHeight * trackHeight / contentHeight;
        thumbHeight = Math.min(Math.max(thumbHeight, 32), trackHeight - 8);

        int thumbY = (int) scrollAmount * (trackHeight - thumbHeight) / scrollRange + top;
        if (thumbY < top) {
            thumbY = top;
        }

        Minecraft.getMinecraft().getTextureManager().bindTexture(TEXTURE);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        // Blend so resource pack sheets carrying an alpha channel render correctly.
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        final Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        // Quads rasterise in submission order, so the thumb covers the track it shares a batch with.
        addBar(tessellator, scrollBarX, top, trackHeight, TRACK_U_OFFSET);
        addBar(tessellator, scrollBarX, thumbY, thumbHeight, THUMB_U_OFFSET);
        tessellator.draw();
        GL11.glDisable(GL11.GL_BLEND);
        // (disable the texture here)
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    }

    /** Appends the quads for one bar. The caller opens and closes the tessellator. */
    private static void addBar(Tessellator tessellator, int x, int y, int height, int u) {
        if (height <= 0) {
            return;
        }
        // Halve the caps on bars under 4px tall so both of them still fit.
        final int capSize = Math.min(SLICE_SIZE, height / 2);
        final int bottomCapY = y + height - capSize;
        addSlice(tessellator, x, y, u, 0, capSize);
        int middleY = y + capSize;
        while (middleY < bottomCapY) {
            // Clip the final band when the gap between the caps is not a multiple of the band height.
            final int sliceHeight = Math.min(SLICE_SIZE, bottomCapY - middleY);
            addSlice(tessellator, x, middleY, u, SLICE_SIZE, sliceHeight);
            middleY += sliceHeight;
        }
        addSlice(tessellator, x, bottomCapY, u, TEXTURE_HEIGHT - capSize, capSize);
    }

    /** Appends one quad covering height rows of the sheet, starting at column u and row v. */
    private static void addSlice(Tessellator tessellator, int x, int y, int u, int v, int height) {
        if (height <= 0) {
            return;
        }
        final float uMin = u / (float) TEXTURE_WIDTH;
        final float uMax = (u + TRACK_WIDTH) / (float) TEXTURE_WIDTH;
        final float vMin = v / (float) TEXTURE_HEIGHT;
        final float vMax = (v + height) / (float) TEXTURE_HEIGHT;
        tessellator.addVertexWithUV(x, y + height, 0.0D, uMin, vMax);
        tessellator.addVertexWithUV(x + TRACK_WIDTH, y + height, 0.0D, uMax, vMax);
        tessellator.addVertexWithUV(x + TRACK_WIDTH, y, 0.0D, uMax, vMin);
        tessellator.addVertexWithUV(x, y, 0.0D, uMin, vMin);
    }
}
