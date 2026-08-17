package com.mitchej123.hodgepodge.mixins.late.thaumcraft;

import java.util.List;

import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionHelper;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mitchej123.hodgepodge.Common;
import com.mitchej123.hodgepodge.util.PotionMetadataCache;

import thaumcraft.common.config.ConfigAspects;

@Mixin(value = ConfigAspects.class, remap = false)
public abstract class MixinConfigAspects_SpeedupPotionAspects {

    @Unique
    private static PotionMetadataCache<List<PotionEffect>> hodgepodge$potionEffectCache;

    @Inject(method = "registerItemAspects", at = @At("HEAD"), require = 1)
    private static void hodgepodge$createPotionEffectCache(CallbackInfo ci) {
        int metadataMask = PotionMetadataCache
                .findRelevantBits(PotionHelper.potionRequirements.values(), PotionHelper.potionAmplifiers.values());

        hodgepodge$potionEffectCache = metadataMask == PotionMetadataCache.ALL_METADATA_BITS ? null
                : new PotionMetadataCache<>(metadataMask);
    }

    @WrapOperation(
            method = "registerItemAspects",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/potion/PotionHelper;getPotionEffects(IZ)Ljava/util/List;",
                    remap = true),
            require = 1)
    private static List<PotionEffect> hodgepodge$cachePotionEffects(int metadata, boolean includeUsable,
            Operation<List<PotionEffect>> original) {
        if (hodgepodge$potionEffectCache == null) return original.call(metadata, includeUsable);
        return hodgepodge$potionEffectCache.get(metadata, value -> original.call(value, includeUsable));
    }

    @Inject(method = "registerItemAspects", at = @At("RETURN"), require = 1)
    private static void hodgepodge$clearPotionEffectCache(CallbackInfo ci) {
        hodgepodge$potionEffectCache = null;
    }
}
