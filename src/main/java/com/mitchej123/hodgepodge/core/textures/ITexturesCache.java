package com.mitchej123.hodgepodge.core.textures;

import net.minecraft.util.IIcon;

import java.util.HashSet;

public interface ITexturesCache {

    HashSet<IIcon> getRenderedTextures();
}
