package com.mitchej123.hodgepodge.core.textures;

public interface IPatchedTextureAtlasSprite {

    void markNeedsAnimationUpdate();
    boolean needsAnimationUpdate();
    void unmarkNeedsAnimationUpdate();

}
