package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.InventoryEffectRenderer;
import net.minecraft.inventory.Container;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(InventoryEffectRenderer.class)
public abstract class MixinInventoryEffectRenderer_PotionOffset extends GuiContainer {

    @Redirect(
            method = "initGui",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/renderer/InventoryEffectRenderer;guiLeft:I",
                    opcode = Opcodes.PUTFIELD))
    public void hodgepodge$fixPotionOffset(InventoryEffectRenderer instance, int value) {
        this.guiLeft = (this.width - this.xSize) / 2;
    }

    /* Forced to have constructor matching super */
    private MixinInventoryEffectRenderer_PotionOffset(Container p_i1072_1_) {
        super(p_i1072_1_);
    }
}
