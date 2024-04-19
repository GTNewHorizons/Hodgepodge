package com.mitchej123.hodgepodge.mixins.early.forge;

import java.util.Arrays;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mitchej123.hodgepodge.config.TweaksConfig;

import cpw.mods.fml.common.IWorldGenerator;
import cpw.mods.fml.common.registry.GameRegistry;

@Mixin(value = GameRegistry.class, remap = false)
public class MixinGameRegistry {

    @Inject(at = @At(remap = false, value = "HEAD"), method = "registerWorldGenerator", cancellable = true)
    private static void hodgepodge$allowListedGenerators(IWorldGenerator generator, int modGenerationWeight,
            CallbackInfo ci) {
        String className = generator.getClass().getName();
        String[] allowList = TweaksConfig.generationClassAllowList;

        if (!Arrays.asList(allowList).contains(className)) {
            ci.cancel();
        }
    }
}
