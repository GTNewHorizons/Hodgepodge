package com.mitchej123.hodgepodge.mixins.minecraft.textures.client;

import com.mitchej123.hodgepodge.core.textures.IPatchedTextureAtlasSprite;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.util.IIcon;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderItem.class)
public class MixinRenderItem {

    /**
     * @author laetansky
     * We can just mark any item texture that gets rendered for an update
     */
    @Inject(method = "renderIcon", at = @At("HEAD"))
    public void beforeRenderIcon(int p_94149_1_, int p_94149_2_, IIcon icon, int p_94149_4_, int p_94149_5_, CallbackInfo ci) {
        ((IPatchedTextureAtlasSprite) icon).markNeedsAnimationUpdate();
    }
}
