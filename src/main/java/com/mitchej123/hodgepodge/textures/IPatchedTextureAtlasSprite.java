package com.mitchej123.hodgepodge.textures;

// No longer used
@Deprecated
public interface IPatchedTextureAtlasSprite {

    void markNeedsAnimationUpdate();

    boolean needsAnimationUpdate();

    void unmarkNeedsAnimationUpdate();

    void updateAnimationsDryRun();
}
