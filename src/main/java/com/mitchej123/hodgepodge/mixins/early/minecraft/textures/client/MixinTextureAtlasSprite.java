package com.mitchej123.hodgepodge.mixins.early.minecraft.textures.client;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import com.mitchej123.hodgepodge.textures.IPatchedTextureAtlasSprite;

@Mixin(TextureAtlasSprite.class)
public class MixinTextureAtlasSprite implements IPatchedTextureAtlasSprite {

    @Unique
    private boolean needsAnimationUpdate = false;

    @Override
    public void markNeedsAnimationUpdate() {
        needsAnimationUpdate = true;
    }

    @Override
    public boolean needsAnimationUpdate() {
        return needsAnimationUpdate;
    }

    @Override
    public void unmarkNeedsAnimationUpdate() {
        needsAnimationUpdate = false;
    }
}
