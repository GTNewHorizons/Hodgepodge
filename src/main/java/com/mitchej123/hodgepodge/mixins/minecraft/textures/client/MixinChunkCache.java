package com.mitchej123.hodgepodge.mixins.minecraft.textures.client;

import com.mitchej123.hodgepodge.core.textures.ITexturesCache;
import net.minecraft.util.IIcon;
import net.minecraft.world.ChunkCache;
import org.spongepowered.asm.mixin.Mixin;

import java.util.HashSet;

@Mixin(ChunkCache.class)
public class MixinChunkCache implements ITexturesCache {

    private final HashSet<IIcon> renderedIcons = new HashSet<>();

    @Override
    public HashSet<IIcon> getRenderedTextures() {
        return renderedIcons;
    }
}
