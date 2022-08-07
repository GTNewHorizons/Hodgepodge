package com.mitchej123.hodgepodge.mixins.minecraft.textures.client;

import com.mitchej123.hodgepodge.core.textures.IPatchedTextureAtlasSprite;
import com.mitchej123.hodgepodge.core.textures.ITexturesCache;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.util.IIcon;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;


@Mixin(RenderGlobal.class)
public class MixinRenderGlobal {

    @ModifyVariable(method = "renderSortedRenderers", at = @At(value = "STORE"))
    private WorldRenderer onRenderSortedRenderers(WorldRenderer worldRenderer) {
        for (IIcon icon : ((ITexturesCache) worldRenderer).getRenderedTextures()) {
            ((IPatchedTextureAtlasSprite) icon).markNeedsAnimationUpdate();
        }
        return worldRenderer;
    }
}
