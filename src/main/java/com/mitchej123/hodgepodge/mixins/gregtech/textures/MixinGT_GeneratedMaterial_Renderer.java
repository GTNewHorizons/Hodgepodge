package com.mitchej123.hodgepodge.mixins.gregtech.textures;

import com.mitchej123.hodgepodge.core.textures.IPatchedTextureAtlasSprite;
import gregtech.api.interfaces.IIconContainer;
import gregtech.common.render.items.GT_GeneratedMaterial_Renderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.IIcon;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(GT_GeneratedMaterial_Renderer.class)
public class MixinGT_GeneratedMaterial_Renderer {
    @Redirect(
            method = "renderItem",
            remap = false,
            at =
                    @At(
                            value = "INVOKE",
                            remap = false,
                            target = "Lgregtech/api/interfaces/IIconContainer;getIcon()Lnet/minecraft/util/IIcon;"))
    public IIcon getIconIconContainer(IIconContainer container) {
        IIcon icon = container.getIcon();
        if (icon instanceof TextureAtlasSprite) {
            ((IPatchedTextureAtlasSprite) icon).markNeedsAnimationUpdate();
        }
        return icon;
    }

    @Redirect(
            method = "renderItem",
            remap = false,
            at =
                    @At(
                            value = "INVOKE",
                            remap = false,
                            target =
                                    "Lgregtech/api/interfaces/IIconContainer;getOverlayIcon()Lnet/minecraft/util/IIcon;"))
    public IIcon getIconOverlayIconContainer(IIconContainer container) {
        IIcon icon = container.getOverlayIcon();
        if (icon instanceof TextureAtlasSprite) {
            ((IPatchedTextureAtlasSprite) icon).markNeedsAnimationUpdate();
        }
        return icon;
    }

    @Redirect(
            method = "renderItem",
            remap = false,
            at =
                    @At(
                            value = "INVOKE",
                            remap = false,
                            target =
                                    "Lnet/minecraftforge/fluids/Fluid;getIcon(Lnet/minecraftforge/fluids/FluidStack;)Lnet/minecraft/util/IIcon;"))
    public IIcon getIconFluid(Fluid instance, FluidStack stack) {
        IIcon icon = instance.getIcon(stack);
        if (icon instanceof TextureAtlasSprite) {
            ((IPatchedTextureAtlasSprite) icon).markNeedsAnimationUpdate();
        }
        return icon;
    }
}
