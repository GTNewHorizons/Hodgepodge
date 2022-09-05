package com.mitchej123.hodgepodge.mixins.minecraft;

import codechicken.nei.NEIClientConfig;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.InventoryEffectRenderer;
import net.minecraft.inventory.Container;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(InventoryEffectRenderer.class)
public abstract class MixinInventoryEffectRenderer_PotionEffectRendering extends GuiContainer {

    @Shadow
    private boolean field_147045_u;

    @Shadow
    private void func_147044_g() {}

    /**
     * @author Alexdoru
     * @reason Fix the bug that renders the potion effects above the tooltips from items in NEI
     */
    @Overwrite
    public void drawScreen(int p_73863_1_, int p_73863_2_, float p_73863_3_) {
        boolean bookmarkPanelHidden = NEIClientConfig.isHidden();
        if (bookmarkPanelHidden) {
            super.drawScreen(p_73863_1_, p_73863_2_, p_73863_3_);
        }
        if (this.field_147045_u) {
            this.func_147044_g();
        }
        if (bookmarkPanelHidden) {
            return;
        }
        super.drawScreen(p_73863_1_, p_73863_2_, p_73863_3_);
    }

    /*Forced to have constructor matching super*/
    public MixinInventoryEffectRenderer_PotionEffectRendering(Container p_i1089_1_) {
        super(p_i1089_1_);
    }
}
