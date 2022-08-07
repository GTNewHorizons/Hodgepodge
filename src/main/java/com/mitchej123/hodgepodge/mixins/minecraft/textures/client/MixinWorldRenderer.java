package com.mitchej123.hodgepodge.mixins.minecraft.textures.client;

import com.mitchej123.hodgepodge.core.textures.ITexturesCache;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.util.IIcon;
import net.minecraft.world.ChunkCache;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.HashSet;

@Mixin(WorldRenderer.class)
public class MixinWorldRenderer implements ITexturesCache {

    private HashSet<IIcon> renderedIcons = new HashSet<>();

    @ModifyVariable(method = "updateRenderer", at = @At(value = "STORE"))
    private ChunkCache onUpdateRenderer(ChunkCache x) {
        renderedIcons = ((ITexturesCache) x).getRenderedTextures();
        return x;
    }

    @Override
    public HashSet<IIcon> getRenderedTextures() {
        return renderedIcons;
    }
}
