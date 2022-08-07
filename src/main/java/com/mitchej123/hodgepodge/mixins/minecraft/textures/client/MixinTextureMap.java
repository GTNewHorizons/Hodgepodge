package com.mitchej123.hodgepodge.mixins.minecraft.textures.client;

import com.mitchej123.hodgepodge.core.textures.IPatchedTextureAtlasSprite;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(TextureMap.class)
public abstract class MixinTextureMap extends AbstractTexture {

    @Shadow
    @Final
    private List<TextureAtlasSprite> listAnimatedSprites;

    /**
     * @author laetansky
     * @reason only update animations for textures that are being currently drawn
     * By default minecraft handles any animations that present in listAnimatedSprites
     * no matter if you see it or not which can lead to a huge performance decrease
     */
    @Overwrite
    public void updateAnimations() {
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.getGlTextureId());
        for (TextureAtlasSprite texture : listAnimatedSprites) {
            if (((IPatchedTextureAtlasSprite) texture).needsAnimationUpdate()) {
                Minecraft.getMinecraft().mcProfiler.startSection(texture.getIconName());
                texture.updateAnimation();
                ((IPatchedTextureAtlasSprite) texture).unmarkNeedsAnimationUpdate();
                Minecraft.getMinecraft().mcProfiler.endSection();
            }
        }
    }
}
