package com.mitchej123.hodgepodge.mixins.late.thaumcraft;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;

import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import thaumcraft.client.renderers.tile.TileAlchemyFurnaceAdvancedRenderer;

@Mixin(value = TileAlchemyFurnaceAdvancedRenderer.class)
public abstract class MixinTileAlchemyFurnaceAdvancedRenderer extends TileEntitySpecialRenderer {

    @Redirect(
            at = @At(
                    target = "Lnet/minecraft/client/renderer/RenderHelper;disableStandardItemLighting()V",
                    value = "INVOKE"),
            method = "renderQuadCenteredFromIcon")
    public void hodgepodge$disableStandardItemLighting() {
        GL11.glDisable(GL11.GL_LIGHTING);
    }

    @Redirect(
            at = @At(
                    target = "Lnet/minecraft/client/renderer/RenderHelper;enableStandardItemLighting()V",
                    value = "INVOKE"),
            method = "renderQuadCenteredFromIcon")
    public void hodgepodge$enableStandardItemLighting() {
        GL11.glEnable(GL11.GL_LIGHTING);
    }
}
