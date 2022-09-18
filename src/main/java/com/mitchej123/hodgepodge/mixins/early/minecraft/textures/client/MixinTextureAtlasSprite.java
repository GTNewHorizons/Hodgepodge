package com.mitchej123.hodgepodge.mixins.early.minecraft.textures.client;

import com.mitchej123.hodgepodge.textures.IPatchedTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(TextureAtlasSprite.class)
public class MixinTextureAtlasSprite implements IPatchedTextureAtlasSprite {

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
