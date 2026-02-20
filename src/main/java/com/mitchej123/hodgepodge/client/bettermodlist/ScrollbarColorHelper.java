package com.mitchej123.hodgepodge.client.bettermodlist;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

public final class ScrollbarColorHelper {

    private static final ResourceLocation SCROLLBAR_TEXTURE = new ResourceLocation(
            "hodgepodge",
            "textures/gui/fml_scrollbar.png");

    private static final int DEFAULT_TRACK = 0x000000;
    private static final int DEFAULT_THUMB = 0x808080;
    private static final int DEFAULT_THUMB_HL = 0xC0C0C0;

    private static boolean loaded;
    private static int trackColor = DEFAULT_TRACK;
    private static int thumbColor = DEFAULT_THUMB;
    private static int thumbHighlightColor = DEFAULT_THUMB_HL;

    private ScrollbarColorHelper() {}

    public static int getTrackColor(Minecraft mc) {
        ensureLoaded(mc);
        return trackColor;
    }

    public static int getThumbColor(Minecraft mc) {
        ensureLoaded(mc);
        return thumbColor;
    }

    public static int getThumbHighlightColor(Minecraft mc) {
        ensureLoaded(mc);
        return thumbHighlightColor;
    }

    private static synchronized void ensureLoaded(Minecraft mc) {
        if (loaded) {
            return;
        }
        loaded = true;

        try (InputStream in = mc.getResourceManager().getResource(SCROLLBAR_TEXTURE).getInputStream()) {
            BufferedImage image = ImageIO.read(in);
            if (image == null || image.getWidth() <= 0 || image.getHeight() < 3) {
                return;
            }

            int segmentHeight = image.getHeight() / 3;
            if (segmentHeight <= 0) {
                return;
            }

            int sampleX = image.getWidth() / 2;
            trackColor = sample(image, sampleX, segmentHeight / 2);
            thumbColor = sample(image, sampleX, segmentHeight + (segmentHeight / 2));
            thumbHighlightColor = sample(image, sampleX, (2 * segmentHeight) + (segmentHeight / 2));
        } catch (IOException ignored) {
            // Keep vanilla colors if the texture is missing or unreadable.
        }
    }

    private static int sample(BufferedImage image, int x, int y) {
        int clampedX = Math.max(0, Math.min(image.getWidth() - 1, x));
        int clampedY = Math.max(0, Math.min(image.getHeight() - 1, y));
        return image.getRGB(clampedX, clampedY) & 0xFFFFFF;
    }
}
