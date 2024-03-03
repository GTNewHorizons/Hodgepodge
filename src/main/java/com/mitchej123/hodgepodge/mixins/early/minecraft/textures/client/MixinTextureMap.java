package com.mitchej123.hodgepodge.mixins.early.minecraft.textures.client;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;

import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import com.mitchej123.hodgepodge.client.HodgepodgeClient;
import com.mitchej123.hodgepodge.client.HodgepodgeClient.AnimationMode;
import com.mitchej123.hodgepodge.textures.IPatchedTextureAtlasSprite;

@Mixin(value = TextureMap.class, priority = 999)
public abstract class MixinTextureMap extends AbstractTexture {

    @Shadow
    @Final
    private List<TextureAtlasSprite> listAnimatedSprites;

    @Unique
    private static final Minecraft MC = Minecraft.getMinecraft();

    /**
     * @author laetansky
     * @reason only update animations for textures that are being currently drawn By default minecraft handles any
     *         animations that present in listAnimatedSprites no matter if you see it or not which can lead to a huge
     *         performance decrease
     */
    @Overwrite
    public void updateAnimations() {
        boolean renderAllAnimations = HodgepodgeClient.animationsMode.is(AnimationMode.ALL);
        boolean renderVisibleAnimations = HodgepodgeClient.animationsMode.is(AnimationMode.VISIBLE_ONLY);

        MC.mcProfiler.startSection("updateAnimations");
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.getGlTextureId());
        // C Style loop should be faster
        int size = listAnimatedSprites.size();
        for (int i = 0; i < size; i++) {
            TextureAtlasSprite textureAtlasSprite = listAnimatedSprites.get(i);
            IPatchedTextureAtlasSprite patchedTextureAtlasSprite = ((IPatchedTextureAtlasSprite) textureAtlasSprite);

            if (renderAllAnimations || (renderVisibleAnimations && patchedTextureAtlasSprite.needsAnimationUpdate())) {
                MC.mcProfiler.startSection(textureAtlasSprite.getIconName());
                textureAtlasSprite.updateAnimation();
                patchedTextureAtlasSprite.unmarkNeedsAnimationUpdate();
                MC.mcProfiler.endSection();
            } else {
                patchedTextureAtlasSprite.updateAnimationsDryRun();
            }
        }
        MC.mcProfiler.endSection();
    }
}
