package com.mitchej123.hodgepodge.mixins.late.gregtech.textures;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.IItemRenderer.ItemRenderType;
import net.minecraftforge.fluids.FluidStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.mitchej123.hodgepodge.textures.IPatchedTextureAtlasSprite;

import gregtech.api.interfaces.IIconContainer;
import gregtech.api.items.GT_MetaGenerated_Item;
import gregtech.common.render.items.GT_GeneratedMaterial_Renderer;

@Mixin(GT_GeneratedMaterial_Renderer.class)
public class MixinGT_GeneratedMaterial_Renderer {

    @Inject(
            at = @At(
                    remap = false,
                    target = "Lgregtech/common/render/items/GT_GeneratedMaterial_Renderer;renderRegularItem(Lnet/minecraftforge/client/IItemRenderer$ItemRenderType;Lnet/minecraft/item/ItemStack;Lnet/minecraft/util/IIcon;Z)V",
                    value = "INVOKE"),
            locals = LocalCapture.CAPTURE_FAILSOFT,
            method = "renderItem",
            remap = false)
    private void hodgepodge$markNeedsAnimationUpdate(ItemRenderType type, ItemStack aStack, Object data[],
            CallbackInfo ci, short aMetaData, GT_MetaGenerated_Item aItem, IIconContainer aIconContainer, IIcon tIcon) {
        if (tIcon instanceof TextureAtlasSprite) {
            ((IPatchedTextureAtlasSprite) tIcon).markNeedsAnimationUpdate();
        }
    }

    @Inject(
            at = @At(
                    remap = false,
                    shift = Shift.AFTER,
                    target = "Lcodechicken/lib/render/TextureUtils;bindAtlas(I)V",
                    value = "INVOKE"),
            locals = LocalCapture.CAPTURE_FAILSOFT,
            method = "renderItem",
            remap = false)
    private void hodgepodge$markNeedsAnimationUpdate(ItemRenderType type, ItemStack aStack, Object data[],
            CallbackInfo ci, short aMetaData, GT_MetaGenerated_Item aItem, IIconContainer aIconContainer, IIcon tIcon,
            IIcon tOverlay) {
        if (tOverlay instanceof TextureAtlasSprite) {
            ((IPatchedTextureAtlasSprite) tOverlay).markNeedsAnimationUpdate();
        }
    }

    @Inject(
            at = @At(
                    remap = false,
                    target = "Lgregtech/common/render/items/GT_GeneratedMaterial_Renderer;renderContainedFluid(Lnet/minecraftforge/client/IItemRenderer$ItemRenderType;Lnet/minecraftforge/fluids/FluidStack;Lnet/minecraft/util/IIcon;)V",
                    value = "INVOKE"),
            locals = LocalCapture.CAPTURE_FAILSOFT,
            method = "renderItem",
            remap = false)
    private void hodgepodge$markNeedsAnimationUpdate(ItemRenderType type, ItemStack aStack, Object data[],
            CallbackInfo ci, short aMetaData, GT_MetaGenerated_Item aItem, IIconContainer aIconContainer, IIcon tIcon,
            IIcon tOverlay, FluidStack aFluid, IIcon fluidIcon) {
        if (fluidIcon instanceof TextureAtlasSprite) {
            ((IPatchedTextureAtlasSprite) fluidIcon).markNeedsAnimationUpdate();
        }
    }
}
