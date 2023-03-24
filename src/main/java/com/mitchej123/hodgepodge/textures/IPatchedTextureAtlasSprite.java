package com.mitchej123.hodgepodge.textures;

public interface IPatchedTextureAtlasSprite {

    void markNeedsAnimationUpdate();

    boolean needsAnimationUpdate();

    void unmarkNeedsAnimationUpdate();

    void updateAnimationsDryRun();
}
