package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.InventoryEffectRenderer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

@Mixin(GuiInventory.class)
public abstract class MixinGuiInventory_MoveCraftingGrid extends InventoryEffectRenderer {

    @Unique
    private static final ResourceLocation hodgepodge$INVENTORY_TEXTURE = new ResourceLocation(
            "hodgepodge",
            "textures/gui/inventory_new.png");

    private MixinGuiInventory_MoveCraftingGrid(Container container) {
        super(container);
    }

    @ModifyArg(
            method = "drawGuiContainerBackgroundLayer",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/texture/TextureManager;bindTexture(Lnet/minecraft/util/ResourceLocation;)V"))
    private ResourceLocation hodgepodge$useNewInventoryTexture(ResourceLocation original) {
        return hodgepodge$INVENTORY_TEXTURE;
    }

    @WrapOperation(
            method = "drawGuiContainerForegroundLayer",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/FontRenderer;drawString(Ljava/lang/String;III)I",
                    ordinal = 0))
    private int hodgepodge$moveCraftingTitle(FontRenderer fontRenderer, String text, int x, int y, int color,
            Operation<Integer> original) {
        return original.call(fontRenderer, text, x + 2, y - 4, color);
    }
}
