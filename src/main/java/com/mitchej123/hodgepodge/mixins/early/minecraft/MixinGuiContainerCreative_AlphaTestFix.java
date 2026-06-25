package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.creativetab.CreativeTabs;

import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * func_147051_a renders one creative tab (background + icon). The icon render calls RenderItem.renderItemIntoGUI which
 * leaves GL_ALPHA_TEST disabled (Forge bug). When the loop over non-selected tabs finishes, the main panel background
 * is drawn next; its transparent cut-outs where adjacent tabs protrude render as black because alpha test is still off.
 * Re-enabling alpha test on every exit of func_147051_a restores the expected state before the main background
 * drawTexturedModalRect.
 */
@Mixin(GuiContainerCreative.class)
public abstract class MixinGuiContainerCreative_AlphaTestFix {

    @Inject(method = "func_147051_a", at = @At("RETURN"))
    private void hodgepodge$restoreAlphaTestAfterTabRender(CreativeTabs tab, CallbackInfo ci) {
        GL11.glEnable(GL11.GL_ALPHA_TEST);
    }
}
