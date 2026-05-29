package com.mitchej123.hodgepodge.mixins.early.minecraft;

import com.mitchej123.hodgepodge.client.IHodgepodgePotionPanelRenderer;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.InventoryEffectRenderer;
import net.minecraft.inventory.Container;

import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(InventoryEffectRenderer.class)
public abstract class MixinInventoryEffectRenderer_PotionEffectRendering extends GuiContainer
        implements IHodgepodgePotionPanelRenderer {

    @Shadow
    private void func_147044_g() {}

    /**
     * @author Alexdoru
     * @reason Suppress vanilla's post-drawScreen call to func_147044_g (gated on field_147045_u which is only set at
     *         initGui, missing effects gained while the GUI is open). The panel is rendered via
     *         MixinGuiContainer_PotionEffectRendering which injects into GuiContainer.drawScreen after
     *         drawGuiContainerForegroundLayer, giving correct z-ordering before the cursor item and tooltips.
     */
    @Overwrite
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void hodgepodge$renderPotionPanelForeground() {
        if (!this.mc.thePlayer.getActivePotionEffects().isEmpty()) {
            GL11.glPushMatrix();
            GL11.glTranslatef(-this.guiLeft, -this.guiTop, 0.0F);
            this.func_147044_g();
            GL11.glPopMatrix();
        }
    }

    /* Forced to have constructor matching super */
    private MixinInventoryEffectRenderer_PotionEffectRendering(Container p_i1089_1_) {
        super(p_i1089_1_);
    }
}
