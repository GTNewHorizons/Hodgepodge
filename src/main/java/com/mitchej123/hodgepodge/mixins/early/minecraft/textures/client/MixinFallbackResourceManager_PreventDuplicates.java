package com.mitchej123.hodgepodge.mixins.early.minecraft.textures.client;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import net.minecraft.client.resources.AbstractResourcePack;
import net.minecraft.client.resources.FallbackResourceManager;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.util.ResourceLocation;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

@Mixin(FallbackResourceManager.class)
public class MixinFallbackResourceManager_PreventDuplicates {

    @Unique
    private Set<File> hodgepodge$visitedJars;

    @Inject(method = "getAllResources", at = @At("HEAD"))
    public void hodgepodge$initSet(ResourceLocation l, CallbackInfoReturnable<?> cir) {
        hodgepodge$visitedJars = new HashSet<>();
    }

    @Inject(method = "getAllResources", at = @At("RETURN"))
    public void hodgepodge$clearSet(ResourceLocation l, CallbackInfoReturnable<?> cir) {
        hodgepodge$visitedJars = null;
    }

    @WrapOperation(
            method = "getAllResources",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/resources/IResourcePack;resourceExists(Lnet/minecraft/util/ResourceLocation;)Z"))
    public boolean hodgepodge$preventDuplicateDiscovery(IResourcePack iresourcepack, ResourceLocation loc,
                                                        Operation<Boolean> exists) {

        if (iresourcepack instanceof AbstractResourcePack abstractPack) {
            if (!hodgepodge$visitedJars.add(abstractPack.resourcePackFile)) {
                return false;
            }
        }

        return exists.call(iresourcepack, loc);
    }
}
