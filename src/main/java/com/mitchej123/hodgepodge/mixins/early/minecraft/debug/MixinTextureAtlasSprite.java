package com.mitchej123.hodgepodge.mixins.early.minecraft.debug;

import java.awt.image.BufferedImage;
import java.util.List;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.data.AnimationMetadataSection;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mitchej123.hodgepodge.util.TexturesDebug;

@Mixin(TextureAtlasSprite.class)
public class MixinTextureAtlasSprite {

    @Shadow
    protected List<int[][]> framesTextureData;

    @Shadow
    @Final
    private String iconName;

    @Shadow
    protected int width;

    @Shadow
    protected int height;

    @Inject(method = "loadSprite", at = @At("RETURN"))
    private void hodgepodge$debug(BufferedImage[] buff, AnimationMetadataSection anim, boolean p_147964_3_,
            CallbackInfo ci) {
        int sizeBytes = 0;
        for (int[][] ints : framesTextureData) {
            for (int[] pixels : ints) {
                if (pixels != null) {
                    sizeBytes += pixels.length * 4;
                }
            }
        }
        TexturesDebug.logTextureAtlasSprite(iconName, width, height, framesTextureData.size(), sizeBytes);
    }

}
