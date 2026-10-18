package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.client.renderer.entity.RenderItem;

import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Forge leaves GL_ALPHA_TEST disabled after drawing flat items, so HUD textures drawn later (like the XP bar) show
 * black instead of transparent pixels. Re-enable it when the method returns.
 */
@Mixin(RenderItem.class)
public abstract class MixinRenderItem_AlphaTestLeak {

    @Inject(
            method = "renderItemIntoGUI(Lnet/minecraft/client/gui/FontRenderer;Lnet/minecraft/client/renderer/texture/TextureManager;Lnet/minecraft/item/ItemStack;IIZ)V",
            at = @At("RETURN"),
            remap = false)
    private void hodgepodge$restoreAlphaTest(CallbackInfo ci) {
        GL11.glEnable(GL11.GL_ALPHA_TEST);
    }
}
