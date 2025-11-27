package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

/**
 * Patched {@link RenderBlocks} to fix {@link #renderFaceYNeg} texture UV
 * <dl>
 * <dt>Suppressed warnings:</dt>
 * <dd>{@code ClassWithTooManyFields}: Legacy class design</dd>
 * <dd>{@code UnusedMixin}: Dynamically applied at run-time</dd>
 * </dl>
 */
@SuppressWarnings({ "ClassWithTooManyFields", "UnusedMixin" })
@Mixin(value = RenderBlocks.class, priority = 100)
public abstract class MixinRenderBlocks_FaceYNegUV {

    @Shadow
    public IIcon overrideBlockTexture;
    @Shadow
    public double renderMinX, renderMaxX, renderMinZ, renderMaxZ, renderMinY;
    @Shadow
    public int uvRotateBottom;
    @Shadow
    public boolean renderFromInside;
    @Shadow
    public boolean enableAO;
    @Shadow
    public int brightnessTopLeft, brightnessTopRight, brightnessBottomLeft, brightnessBottomRight;
    @Shadow
    public float colorRedTopLeft, colorGreenTopLeft, colorBlueTopLeft;
    @Shadow
    public float colorRedTopRight, colorGreenTopRight, colorBlueTopRight;
    @Shadow
    public float colorRedBottomLeft, colorGreenBottomLeft, colorBlueBottomLeft;
    @Shadow
    public float colorRedBottomRight, colorGreenBottomRight, colorBlueBottomRight;

    @Shadow
    public abstract boolean hasOverrideBlockTexture();

    /**
     * @author leagris
     * @reason Fixes horizontal flip bug in bottom face textures prior to Minecraft 1.8.
     * @link <a href="https://bugs.mojang.com/browse/MC-47811">MC-47811</a>
     */
    @SuppressWarnings("OverlyComplexMethod") // Complex by nature
    @Overwrite
    public void renderFaceYNeg(Block block, double x, double y, double z, IIcon icon) {
        Tessellator tessellator = Tessellator.instance;

        if (this.hasOverrideBlockTexture()) {
            icon = this.overrideBlockTexture;
        }

        double uMinX = icon.getInterpolatedU(this.renderMaxX * 16.0D);
        double uMaxX = icon.getInterpolatedU(this.renderMinX * 16.0D);
        double vMinZ = icon.getInterpolatedV(this.renderMinZ * 16.0D);
        double vMaxZ = icon.getInterpolatedV(this.renderMaxZ * 16.0D);

        if (this.renderMinX < 0.0D || this.renderMaxX > 1.0D) {
            uMinX = icon.getMaxU();
            uMaxX = icon.getMinU();
        }

        if (this.renderMinZ < 0.0D || this.renderMaxZ > 1.0D) {
            vMinZ = icon.getMinV();
            vMaxZ = icon.getMaxV();
        }

        double uMaxXCopy = uMaxX;
        double uMinXCopy = uMinX;
        double vMinZCopy = vMinZ;
        double vMaxZCopy = vMaxZ;

        if (this.uvRotateBottom == 2) {
            uMinX = icon.getInterpolatedU(this.renderMaxZ * 16.0D);
            vMinZ = icon.getInterpolatedV(16.0D - this.renderMaxX * 16.0D);
            uMaxX = icon.getInterpolatedU(this.renderMinZ * 16.0D);
            vMaxZ = icon.getInterpolatedV(16.0D - this.renderMinX * 16.0D);
            vMinZCopy = vMinZ;
            vMaxZCopy = vMaxZ;
            uMaxXCopy = uMinX;
            uMinXCopy = uMaxX;
            vMinZ = vMaxZ;
            vMaxZ = vMinZCopy;
        } else if (this.uvRotateBottom == 1) {
            uMinX = icon.getInterpolatedU(16.0D - this.renderMinZ * 16.0D);
            vMinZ = icon.getInterpolatedV(this.renderMinX * 16.0D);
            uMaxX = icon.getInterpolatedU(16.0D - this.renderMaxZ * 16.0D);
            vMaxZ = icon.getInterpolatedV(this.renderMaxX * 16.0D);
            uMaxXCopy = uMaxX;
            uMinXCopy = uMinX;
            uMinX = uMaxX;
            uMaxX = uMinXCopy;
            vMinZCopy = vMaxZ;
            vMaxZCopy = vMinZ;
        } else if (this.uvRotateBottom == 3) {
            uMinX = icon.getInterpolatedU(16.0D - this.renderMaxX * 16.0D);
            uMaxX = icon.getInterpolatedU(16.0D - this.renderMinX * 16.0D);
            vMinZ = icon.getInterpolatedV(16.0D - this.renderMinZ * 16.0D);
            vMaxZ = icon.getInterpolatedV(16.0D - this.renderMaxZ * 16.0D);
            uMaxXCopy = uMaxX;
            uMinXCopy = uMinX;
            vMinZCopy = vMinZ;
            vMaxZCopy = vMaxZ;
        }

        double xMin = x + this.renderMinX;
        double xMax = x + this.renderMaxX;
        double yMin = y + this.renderMinY;
        double zMin = z + this.renderMinZ;
        double zMax = z + this.renderMaxZ;

        if (this.renderFromInside) {
            xMin = x + this.renderMaxX;
            xMax = x + this.renderMinX;
        }

        if (this.enableAO) {
            tessellator.setColorOpaque_F(this.colorRedTopLeft, this.colorGreenTopLeft, this.colorBlueTopLeft);
            tessellator.setBrightness(this.brightnessTopLeft);
            tessellator.addVertexWithUV(xMin, yMin, zMax, uMinXCopy, vMaxZCopy);
            tessellator.setColorOpaque_F(this.colorRedBottomLeft, this.colorGreenBottomLeft, this.colorBlueBottomLeft);
            tessellator.setBrightness(this.brightnessBottomLeft);
            tessellator.addVertexWithUV(xMin, yMin, zMin, uMinX, vMinZ);
            tessellator
                    .setColorOpaque_F(this.colorRedBottomRight, this.colorGreenBottomRight, this.colorBlueBottomRight);
            tessellator.setBrightness(this.brightnessBottomRight);
            tessellator.addVertexWithUV(xMax, yMin, zMin, uMaxXCopy, vMinZCopy);
            tessellator.setColorOpaque_F(this.colorRedTopRight, this.colorGreenTopRight, this.colorBlueTopRight);
            tessellator.setBrightness(this.brightnessTopRight);
            tessellator.addVertexWithUV(xMax, yMin, zMax, uMaxX, vMaxZ);
        } else {
            tessellator.addVertexWithUV(xMin, yMin, zMax, uMinXCopy, vMaxZCopy);
            tessellator.addVertexWithUV(xMin, yMin, zMin, uMinX, vMinZ);
            tessellator.addVertexWithUV(xMax, yMin, zMin, uMaxXCopy, vMinZCopy);
            tessellator.addVertexWithUV(xMax, yMin, zMax, uMaxX, vMaxZ);
        }
    }
}
