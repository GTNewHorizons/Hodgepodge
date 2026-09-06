package com.mitchej123.hodgepodge.mixins.early.minecraft.fastload;

import net.minecraft.client.renderer.texture.TextureMap;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(TextureMap.class)
public class MixinTextureMap_RemoveExtraIconLoad {

    /**
     * Removes an unnecessary call to registerIcons in the constructor
     */
    @Redirect(
            method = "<init>(ILjava/lang/String;Z)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/texture/TextureMap;registerIcons()V"))
    private void hodgepodge$dontRegisterIconsInInit(TextureMap instance) {}

}
