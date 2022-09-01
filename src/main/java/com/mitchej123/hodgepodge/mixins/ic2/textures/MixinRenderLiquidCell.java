package com.mitchej123.hodgepodge.mixins.ic2.textures;

import com.mitchej123.hodgepodge.core.textures.IPatchedTextureAtlasSprite;
import ic2.core.item.RenderLiquidCell;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.IIcon;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(RenderLiquidCell.class)
public class MixinRenderLiquidCell {
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
