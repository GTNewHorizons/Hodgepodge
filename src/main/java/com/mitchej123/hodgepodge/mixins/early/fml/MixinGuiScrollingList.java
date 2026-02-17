package com.mitchej123.hodgepodge.mixins.early.fml;

import java.io.IOException;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mitchej123.hodgepodge.mixins.interfaces.IGuiScrollingList;

import cpw.mods.fml.client.GuiScrollingList;

@Mixin(GuiScrollingList.class)
public class MixinGuiScrollingList implements IGuiScrollingList {

    @Unique
    private static final ResourceLocation hodgepodge$SCROLLBAR_TEXTURE = new ResourceLocation(
            "hodgepodge",
            "textures/gui/fml_scrollbar.png");
    @Unique
    private static final int hodgepodge$SCROLLBAR_TRACK = 0xFF000000;
    @Unique
    private static final int hodgepodge$SCROLLBAR_THUMB = 0xFF808080;
    @Unique
    private static final int hodgepodge$SCROLLBAR_THUMB_HL = 0xFFC0C0C0;

    @Shadow(remap = false)
    @Final
    private Minecraft client;

    @Shadow(remap = false)
    @Final
    protected int listWidth;

    @Shadow(remap = false)
    @Final
    protected int bottom;

    @Shadow(remap = false)
    @Final
    protected int top;

    @Shadow(remap = false)
    @Final
    protected int left;

    @Shadow(remap = false)
    @Final
    private int right;

    @Shadow(remap = false)
    private int selectedIndex;

    @Unique
    private boolean hodgepodge$checkedScrollbarTexture;
    @Unique
    private boolean hodgepodge$hasScrollbarTexture;

    @Inject(
            method = "drawScreen",
            at = @At(value = "INVOKE", target = "cpw/mods/fml/client/GuiScrollingList.applyScrollLimits()V"),
            remap = false)
    private void hodgepodge$glEnableScissor(CallbackInfo ci) {
        ScaledResolution res = new ScaledResolution(client, client.displayWidth, client.displayHeight);
        double scaleW = client.displayWidth / res.getScaledWidth_double();
        double scaleH = client.displayHeight / res.getScaledHeight_double();
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor(
                (int) (left * scaleW),
                (int) (client.displayHeight - (bottom * scaleH)),
                (int) (listWidth * scaleW),
                (int) ((this.bottom - this.top) * scaleH));
    }

    @Redirect(
            method = "drawScreen",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;drawRect(IIIII)V"),
            remap = false)
    private void hodgepodge$drawCustomScrollbarRect(int x1, int y1, int x2, int y2, int color) {
        int part = hodgepodge$getScrollbarPart(x1, y1, x2, y2, color);
        if (part < 0 || !hodgepodge$hasScrollbarTexture()) {
            Gui.drawRect(x1, y1, x2, y2, color);
            return;
        }

        hodgepodge$drawScrollbarTexturePart(x1, y1, x2, y2, part);
    }

    @Inject(method = "drawScreen", at = @At("TAIL"), remap = false)
    private void hodgepodge$glDisableScissor(CallbackInfo ci) {
        GL11.glDisable(GL11.GL_SCISSOR_TEST);

    }

    @Unique
    private int hodgepodge$getScrollbarPart(int x1, int y1, int x2, int y2, int color) {
        int width = x2 - x1;
        if (width < 4 || width > 8) {
            return -1;
        }
        if (x1 < right - 10 || x2 > right + 2) {
            return -1;
        }
        if (y2 < top || y1 > bottom) {
            return -1;
        }

        if (color == hodgepodge$SCROLLBAR_TRACK) {
            return 0;
        }
        if (color == hodgepodge$SCROLLBAR_THUMB) {
            return 1;
        }
        if (color == hodgepodge$SCROLLBAR_THUMB_HL) {
            return 2;
        }

        return -1;
    }

    @Unique
    private boolean hodgepodge$hasScrollbarTexture() {
        if (!hodgepodge$checkedScrollbarTexture) {
            hodgepodge$checkedScrollbarTexture = true;
            try {
                client.getResourceManager().getResource(hodgepodge$SCROLLBAR_TEXTURE);
                hodgepodge$hasScrollbarTexture = true;
            } catch (IOException ignored) {
                hodgepodge$hasScrollbarTexture = false;
            }
        }
        return hodgepodge$hasScrollbarTexture;
    }

    @Unique
    private void hodgepodge$drawScrollbarTexturePart(int x1, int y1, int x2, int y2, int part) {
        boolean texturesEnabled = GL11.glIsEnabled(GL11.GL_TEXTURE_2D);
        boolean blendEnabled = GL11.glIsEnabled(GL11.GL_BLEND);
        if (!texturesEnabled) {
            GL11.glEnable(GL11.GL_TEXTURE_2D);
        }
        if (!blendEnabled) {
            GL11.glEnable(GL11.GL_BLEND);
        }

        client.getTextureManager().bindTexture(hodgepodge$SCROLLBAR_TEXTURE);
        GL11.glColor4f(1, 1, 1, 1);

        double v0 = part / 3.0D;
        double v1 = (part + 1) / 3.0D;
        Tessellator t = Tessellator.instance;
        t.startDrawingQuads();
        t.addVertexWithUV(x1, y2, 0, 0, v1);
        t.addVertexWithUV(x2, y2, 0, 1, v1);
        t.addVertexWithUV(x2, y1, 0, 1, v0);
        t.addVertexWithUV(x1, y1, 0, 0, v0);
        t.draw();

        if (!texturesEnabled) {
            GL11.glDisable(GL11.GL_TEXTURE_2D);
        }
        if (!blendEnabled) {
            GL11.glDisable(GL11.GL_BLEND);
        }
    }

    @Override
    public int hodgepodge$setSelectedIndex(int index) {
        return selectedIndex = index;
    }

    @Override
    public int hodgepodge$getBottom() {
        return bottom;
    }

    @Override
    public int hodgepodge$getRight() {
        return right;
    }
}
