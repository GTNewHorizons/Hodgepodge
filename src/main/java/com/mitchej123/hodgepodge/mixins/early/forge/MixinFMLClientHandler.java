package com.mitchej123.hodgepodge.mixins.early.forge;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.mitchej123.hodgepodge.mixins.hooks.IC2ResourcePack;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.ModContainer;

@Mixin(FMLClientHandler.class)
public class MixinFMLClientHandler {

    @Redirect(
            method = "addModAsResource",
            at = @At(
                    value = "INVOKE",
                    target = "Lcpw/mods/fml/common/ModContainer;getCustomResourcePackClass()Ljava/lang/Class;"),
            remap = false)
    private Class<?> hodgepodge$redirectIC2CustomResourcePackClass(ModContainer instance) {
        if ("IC2".equals(instance.getModId())) {
            return IC2ResourcePack.class;
        }
        return instance.getCustomResourcePackClass();
    }
}
