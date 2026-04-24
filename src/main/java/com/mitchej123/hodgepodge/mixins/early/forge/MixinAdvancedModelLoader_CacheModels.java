package com.mitchej123.hodgepodge.mixins.early.forge;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;

@Mixin(value = AdvancedModelLoader.class, remap = false)
public class MixinAdvancedModelLoader_CacheModels {

    @Unique
    private static final Map<ResourceLocation, WeakReference<IModelCustom>> hp$ModelCache = new ConcurrentHashMap<>();

    @Inject(method = "loadModel", at = @At("HEAD"), cancellable = true)
    private static void returnCachedModel(ResourceLocation resource, CallbackInfoReturnable<IModelCustom> cir) {
        final WeakReference<IModelCustom> cachedRef = hp$ModelCache.get(resource);
        if (cachedRef != null) {
            final IModelCustom cachedModel = cachedRef.get();
            if (cachedModel != null) {
                cir.setReturnValue(cachedModel);
            }
        }
    }

    @ModifyReturnValue(method = "loadModel", at = @At("RETURN"))
    private static IModelCustom cacheModel(IModelCustom original, ResourceLocation resource) {
        hp$ModelCache.put(resource, new WeakReference<>(original));
        return original;
    }
}
