package com.mitchej123.hodgepodge.mixins.early.minecraft;

import java.util.Set;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.relauncher.Side;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;

@Mixin(value = LanguageRegistry.class, remap = false)
public class MixinLanguageRegistry {

    private Set<String> examinedFiles = new ObjectOpenHashSet<>();

    @Inject(method = "loadLanguagesFor", at = @At("HEAD"), cancellable = true)
    private void hodgepodge$onlyAddLanguagesOnce(ModContainer container, Side side, CallbackInfo ci) {
        if (!examinedFiles.add(container.getSource().getName())) {
            ci.cancel();
        }
    }
}
