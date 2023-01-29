package com.mitchej123.hodgepodge.mixins.early.minecraft.textures.client;

import java.util.HashSet;

import net.minecraft.util.IIcon;
import net.minecraft.world.ChunkCache;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import com.mitchej123.hodgepodge.textures.ITexturesCache;

@Mixin(ChunkCache.class)
public class MixinChunkCache implements ITexturesCache {

    @Unique
    private final HashSet<IIcon> renderedIcons = new HashSet<>();

    @Override
    public HashSet<IIcon> getRenderedTextures() {
        return renderedIcons;
    }
}
