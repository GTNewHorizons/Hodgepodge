package com.mitchej123.hodgepodge.mixins.early.minecraft;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiSlot;
import net.minecraft.util.ResourceLocation;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(GuiSlot.class)
public class MixinGuiSlot_Scrollbar {

    @Unique
    private static final ResourceLocation hodgepodge$SCROLLBAR_TEXTURE = new ResourceLocation(
            "hodgepodge",
            "textures/gui/fml_scrollbar.png");
    @Unique
    private static final int hodgepodge$SCROLLBAR_TRACK = 0x000000;
    @Unique
    private static final int hodgepodge$SCROLLBAR_THUMB = 0x808080;
    @Unique
    private static final int hodgepodge$SCROLLBAR_THUMB_HL = 0xC0C0C0;

    @Shadow
    @Final
    private Minecraft mc;

    @Unique
    private boolean hodgepodge$checkedScrollbarTexture;
    @Unique
    private int hodgepodge$scrollbarTrackColor = hodgepodge$SCROLLBAR_TRACK;
    @Unique
    private int hodgepodge$scrollbarThumbColor = hodgepodge$SCROLLBAR_THUMB;
    @Unique
    private int hodgepodge$scrollbarThumbHighlightColor = hodgepodge$SCROLLBAR_THUMB_HL;

    @ModifyArgs(
            method = "drawScreen",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/Tessellator;setColorRGBA_I(II)V",
                    ordinal = 4))
    private void hodgepodge$recolorScrollbarTrack(Args args) {
        hodgepodge$loadScrollbarColors();
        args.set(0, hodgepodge$scrollbarTrackColor);
    }

    @ModifyArgs(
            method = "drawScreen",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/Tessellator;setColorRGBA_I(II)V",
                    ordinal = 5))
    private void hodgepodge$recolorScrollbarThumb(Args args) {
        hodgepodge$loadScrollbarColors();
        args.set(0, hodgepodge$scrollbarThumbColor);
    }

    @ModifyArgs(
            method = "drawScreen",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/Tessellator;setColorRGBA_I(II)V",
                    ordinal = 6))
    private void hodgepodge$recolorScrollbarThumbHighlight(Args args) {
        hodgepodge$loadScrollbarColors();
        args.set(0, hodgepodge$scrollbarThumbHighlightColor);
    }

    @Unique
    private void hodgepodge$loadScrollbarColors() {
        if (!hodgepodge$checkedScrollbarTexture) {
            hodgepodge$checkedScrollbarTexture = true;
            try (InputStream in = mc.getResourceManager().getResource(hodgepodge$SCROLLBAR_TEXTURE).getInputStream()) {
                BufferedImage image = ImageIO.read(in);
                if (image != null && image.getWidth() > 0 && image.getHeight() >= 3) {
                    int segmentHeight = image.getHeight() / 3;
                    if (segmentHeight > 0) {
                        int sampleX = image.getWidth() / 2;
                        hodgepodge$scrollbarTrackColor = hodgepodge$getPixelColor(image, sampleX, segmentHeight / 2);
                        hodgepodge$scrollbarThumbColor = hodgepodge$getPixelColor(
                                image,
                                sampleX,
                                segmentHeight + (segmentHeight / 2));
                        hodgepodge$scrollbarThumbHighlightColor = hodgepodge$getPixelColor(
                                image,
                                sampleX,
                                (2 * segmentHeight) + (segmentHeight / 2));
                    }
                }
            } catch (IOException ignored) {
                // Keep vanilla colors when the texture is absent or unreadable.
            }
        }
    }

    @Unique
    private int hodgepodge$getPixelColor(BufferedImage image, int x, int y) {
        int clampedX = Math.max(0, Math.min(image.getWidth() - 1, x));
        int clampedY = Math.max(0, Math.min(image.getHeight() - 1, y));
        return image.getRGB(clampedX, clampedY) & 0xFFFFFF;
    }
}
