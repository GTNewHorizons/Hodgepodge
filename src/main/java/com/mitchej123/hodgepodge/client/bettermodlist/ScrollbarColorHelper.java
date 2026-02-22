package com.mitchej123.hodgepodge.client.bettermodlist;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.util.ResourceLocation;

/**
 * Resource location for the scrollbar color definition texture.
 * <p>
 * This texture is expected to be provided by the {@code hodgepodge} mod assets or by a resource pack at:
 * 
 * <pre>
 * assets / hodgepodge / textures / gui / fml_scrollbar.png
 * </pre>
 * 
 * The image is sampled vertically in three equal segments:
 * <ul>
 * <li>Top third: scrollbar track color</li>
 * <li>Middle third: scrollbar thumb color</li>
 * <li>Bottom third: scrollbar thumb highlight color</li>
 * </ul>
 * If this texture is missing or unreadable, the helper falls back to the hard-coded default colors defined below.
 */
public final class ScrollbarColorHelper implements IResourceManagerReloadListener {

    private static final ResourceLocation SCROLLBAR_TEXTURE = new ResourceLocation(
            "hodgepodge",
            "textures/gui/fml_scrollbar.png");

    private static final int DEFAULT_TRACK = 0x000000;
    private static final int DEFAULT_THUMB = 0x808080;
    private static final int DEFAULT_THUMB_HL = 0xC0C0C0;

    private static final ScrollbarColorHelper INSTANCE = new ScrollbarColorHelper();

    private static boolean initialized;
    private static int trackColor = DEFAULT_TRACK;
    private static int thumbColor = DEFAULT_THUMB;
    private static int thumbHighlightColor = DEFAULT_THUMB_HL;

    private ScrollbarColorHelper() {}

    public static int getTrackColor(Minecraft mc) {
        ensureInitialized(mc);
        return trackColor;
    }

    public static int getThumbColor(Minecraft mc) {
        ensureInitialized(mc);
        return thumbColor;
    }

    public static int getThumbHighlightColor(Minecraft mc) {
        ensureInitialized(mc);
        return thumbHighlightColor;
    }

    private static synchronized void ensureInitialized(Minecraft mc) {
        if (initialized) {
            return;
        }
        initialized = true;

        IResourceManager resourceManager = mc.getResourceManager();
        if (resourceManager instanceof IReloadableResourceManager) {
            ((IReloadableResourceManager) resourceManager).registerReloadListener(INSTANCE);
        }

        INSTANCE.onResourceManagerReload(resourceManager);
    }

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {
        trackColor = DEFAULT_TRACK;
        thumbColor = DEFAULT_THUMB;
        thumbHighlightColor = DEFAULT_THUMB_HL;

        try (InputStream in = resourceManager.getResource(SCROLLBAR_TEXTURE).getInputStream()) {
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
            // Keep defaults if the texture is missing or unreadable.
        }
    }

    private static int sample(BufferedImage image, int x, int y) {
        int clampedX = Math.max(0, Math.min(image.getWidth() - 1, x));
        int clampedY = Math.max(0, Math.min(image.getHeight() - 1, y));
        return image.getRGB(clampedX, clampedY) & 0xFFFFFF;
    }
}
