package com.mitchej123.hodgepodge.mixins.minecraft.textures.client.fastcraft;

import net.minecraft.client.renderer.texture.TextureMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@SuppressWarnings({"UnresolvedMixinReference", "InvalidInjectorMethodSignature", "MixinAnnotationTarget"})
@Mixin(TextureMap.class)
public abstract class MixinTextureMap {

    @Redirect(method = "tick",
            at = @At(value = "INVOKE",
                    target = "Lfastcraft/HC;h(Lnet/minecraft/client/renderer/texture/TextureMap;)V",
                    remap = false))
    private void disableUpdateAnimationsTweak(TextureMap map) {
        map.updateAnimations();
    }
}
