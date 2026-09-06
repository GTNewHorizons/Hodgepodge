package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.client.renderer.InventoryEffectRenderer;

import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Forge's RenderItem.renderItemIntoGUI disables GL_ALPHA_TEST after rendering flat 2D items but InventoryEffectRenderer
 * never re-enables it, so the transparent corners of the potion-effect background panel render as opaque rectangles.
 */
@Mixin(InventoryEffectRenderer.class)
public abstract class MixinInventoryEffectRenderer_AlphaTestFix {

    @Inject(method = "func_147044_g", at = @At("HEAD"))
    private void hodgepodge$enableAlphaTestForPotionPanel(CallbackInfo ci) {
        GL11.glEnable(GL11.GL_ALPHA_TEST);
    }
}
