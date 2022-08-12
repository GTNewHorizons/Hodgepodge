package com.mitchej123.hodgepodge.mixins.minecraft.textures.client;

import com.mitchej123.hodgepodge.core.textures.IPatchedTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.MinecraftForgeClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftForgeClient.class)
public class MixinMinecraftForgeClient {

    /**
     * @author laetansky
     * We can just mark any item texture that gets rendered for an update
     */
    @Inject(method = "getItemRenderer", at = @At("HEAD"), remap = false)
    private static void beforeRenderItem(ItemStack item, IItemRenderer.ItemRenderType type, CallbackInfoReturnable<IItemRenderer> cir) {
        IIcon icon = item.getIconIndex();
        if (icon instanceof TextureAtlasSprite) {
            ((IPatchedTextureAtlasSprite) icon).markNeedsAnimationUpdate();
        }
    }
}
