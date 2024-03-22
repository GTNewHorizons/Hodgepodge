package com.mitchej123.hodgepodge.mixins.early.ic2;

import java.util.HashMap;
import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import cpw.mods.fml.common.registry.LanguageRegistry;
import ic2.core.init.Localization;

@Mixin(Localization.class)
public class MixinLocalization {

    /**
     * Translations are delegated to vanilla lang system
     * 
     * @see com.mitchej123.hodgepodge.mixins.early.forge.MixinLanguageRegistry
     * @see com.mitchej123.hodgepodge.mixins.hooks.IC2ResourcePack
     */
    @Redirect(
            method = "postInit",
            at = @At(
                    value = "INVOKE",
                    target = "Lcpw/mods/fml/common/registry/LanguageRegistry;injectLanguage(Ljava/lang/String;Ljava/util/HashMap;)V"),
            remap = false)
    private static void hodgepodge$redirectInjectLanguage(LanguageRegistry instance, String language,
            HashMap<String, String> parsedLangFile) {}

    /**
     * @author miozune
     * @reason Translations are delegated to vanilla lang system
     * @see com.mitchej123.hodgepodge.mixins.early.forge.MixinLanguageRegistry
     * @see com.mitchej123.hodgepodge.mixins.hooks.IC2ResourcePack
     */
    @Overwrite(remap = false)
    protected static Map<String, String> getStringTranslateMap() {
        return new HashMap<>();
    }
}
