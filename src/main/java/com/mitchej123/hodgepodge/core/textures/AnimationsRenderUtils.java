package com.mitchej123.hodgepodge.core.textures;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

public class AnimationsRenderUtils {

    public static void markBlockTextureForUpdate(IIcon icon, IBlockAccess blockAccess) {
        TextureMap textureMap = Minecraft.getMinecraft().getTextureMapBlocks();
        TextureAtlasSprite textureAtlasSprite = textureMap.getAtlasSprite(icon.getIconName());

        if (textureAtlasSprite != null && textureAtlasSprite.hasAnimationMetadata()) {
            // null if called by anything but chunk render cache update (for example to get blocks rendered as items in inventory)
            if (blockAccess instanceof ITexturesCache) {
                ((ITexturesCache) blockAccess).getRenderedTextures().add(textureAtlasSprite);
            } else {
                ((IPatchedTextureAtlasSprite) textureAtlasSprite).markNeedsAnimationUpdate();
            }
        }
    }
}
