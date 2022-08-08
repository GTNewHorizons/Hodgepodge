package com.mitchej123.hodgepodge.mixins.minecraft.textures.client;

import com.mitchej123.hodgepodge.core.textures.IPatchedTextureAtlasSprite;
import com.mitchej123.hodgepodge.core.textures.ITexturesCache;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Set;

@Mixin(WorldRenderer.class)
public class MixinWorldRenderer implements ITexturesCache {

    @Shadow
    public boolean isInFrustum;

    private Set<IIcon> renderedIcons;

    @ModifyArg(method = "updateRenderer", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderBlocks;<init>(Lnet/minecraft/world/IBlockAccess;)V"))
    private IBlockAccess onUpdateRenderer(IBlockAccess chunkCache) {
        renderedIcons = ((ITexturesCache) chunkCache).getRenderedTextures();
        return chunkCache;
    }

    @Inject(method = "getGLCallListForPass", at = @At("HEAD"))
    private void getGLCallListForPass(int pass, CallbackInfoReturnable<Integer> cir) {
        if (isInFrustum && pass == 0) {
            for (IIcon icon : getRenderedTextures()) {
                ((IPatchedTextureAtlasSprite) icon).markNeedsAnimationUpdate();
            }
        }
    }

    @Override
    public Set<IIcon> getRenderedTextures() {
        return renderedIcons;
    }
}
