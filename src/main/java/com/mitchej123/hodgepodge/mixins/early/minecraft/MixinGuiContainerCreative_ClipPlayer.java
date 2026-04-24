package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.renderer.InventoryEffectRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.Container;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mitchej123.hodgepodge.mixins.hooks.GLScissorHelper;

@Mixin(GuiContainerCreative.class)
public abstract class MixinGuiContainerCreative_ClipPlayer extends InventoryEffectRenderer {

    private MixinGuiContainerCreative_ClipPlayer(Container p_i1089_1_) {
        super(p_i1089_1_);
    }

    @WrapOperation(
            method = "drawGuiContainerBackgroundLayer",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/inventory/GuiInventory;func_147046_a(IIIFFLnet/minecraft/entity/EntityLivingBase;)V"))
    private void enableClipping(int x, int y, int scale, float relMouseX, float relMouseY, EntityLivingBase entity,
            Operation<Void> original) {
        GLScissorHelper.glScissorByGuiCoords(mc, guiLeft + 28, guiTop + 6, 32, 43);
        original.call(x, y, scale, relMouseX, relMouseY, entity);
        GLScissorHelper.endGlScissor();
    }
}
