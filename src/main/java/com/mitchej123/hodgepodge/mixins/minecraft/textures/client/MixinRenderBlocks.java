package com.mitchej123.hodgepodge.mixins.minecraft.textures.client;

import com.mitchej123.hodgepodge.core.textures.IPatchedTextureAtlasSprite;
import com.mitchej123.hodgepodge.core.textures.ITexturesCache;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderBlocks.class)
public class MixinRenderBlocks {

    @Shadow
    public IBlockAccess blockAccess;
    @Shadow
    public IIcon overrideBlockTexture;

    /**
     * @author laetansky
     * Here where things get very tricky. We can't just mark blocks textures for update
     * because this method gets called only when chunk render cache needs an update (that happens when a state
     * of any block in that chunk changes).
     * What we can do though is pass the rendered textures up to the WorldRenderer and later use it in RenderGlobal to
     * mark textures for update and before that even sort WorldRenderers and apply Occlusion Querry (Basically that means
     * that we will only mark those textures for update that are visible (on the viewport) at the moment)
     */
    @Inject(method = "*(Lnet/minecraft/block/Block;DDDLnet/minecraft/util/IIcon;)V", at = @At("HEAD"))
    public void beforeRenderFace(Block p_147761_1_, double p_147761_2_, double p_147761_4_, double p_147761_6_, IIcon icon, CallbackInfo ci) {
        if (overrideBlockTexture != null) {
            icon = overrideBlockTexture;
        }

        markBlockTextureForUpdate(icon);
    }

    @ModifyVariable(method = "renderBlockLiquid", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/client/renderer/RenderBlocks;getBlockIconFromSideAndMetadata(Lnet/minecraft/block/Block;II)Lnet/minecraft/util/IIcon;"))
    public IIcon markFluidAnimationForUpdate(IIcon icon) {
        markBlockTextureForUpdate(icon);

        return icon;
    }

    private void markBlockTextureForUpdate(IIcon icon) {
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
