package com.mitchej123.hodgepodge.mixins.early.fml;

import net.minecraft.client.Minecraft;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mitchej123.hodgepodge.client.sound.BootSoundReload;

import cpw.mods.fml.client.FMLClientHandler;

@Mixin(FMLClientHandler.class)
public class MixinFMLClientHandler_BootSoundRefresh {

    @WrapOperation(
            method = "finishMinecraftLoading",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;refreshResources()V", remap = true),
            remap = false)
    private void hodgepodge$bootRefresh(Minecraft client, Operation<Void> original) {
        BootSoundReload.beginBootRefresh();
        try {
            original.call(client);
        } finally {
            BootSoundReload.endBootRefresh();
        }
    }
}
