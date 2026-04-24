package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiScreenHorseInventory;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.Container;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mitchej123.hodgepodge.mixins.hooks.GLScissorHelper;

@Mixin(GuiScreenHorseInventory.class)
public abstract class MixinGuiScreenHorseInventory_ClipPlayer extends GuiContainer {

    private MixinGuiScreenHorseInventory_ClipPlayer(Container p_i1089_1_) {
        super(p_i1089_1_);
    }

    @WrapOperation(
            method = "drawGuiContainerBackgroundLayer",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/inventory/GuiInventory;func_147046_a(IIIFFLnet/minecraft/entity/EntityLivingBase;)V"))
    private void enableClipping(int x, int y, int scale, float relMouseX, float relMouseY, EntityLivingBase entity,
            Operation<Void> original) {
        GLScissorHelper.glScissorByGuiCoords(mc, guiLeft + 26, guiTop + 18, 52, 52);
        original.call(x, y, scale, relMouseX, relMouseY, entity);
        GLScissorHelper.endGlScissor();
    }
}
