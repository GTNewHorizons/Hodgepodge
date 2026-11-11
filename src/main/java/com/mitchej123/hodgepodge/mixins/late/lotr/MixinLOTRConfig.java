package com.mitchej123.hodgepodge.mixins.late.lotr;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import lotr.common.LOTRConfig;

@Mixin(value = LOTRConfig.class, remap = false)
public class MixinLOTRConfig {

    @Shadow
    private static String CATEGORY_MISC;

    /**
     * Set updateLangFiles to false by default, as it is incompatible with the Gradle cache (breaking dev environments)
     * and very rarely needed
     */
    @Redirect(
            method = "load()V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraftforge/common/config/Configuration;get(Ljava/lang/String;Ljava/lang/String;ZLjava/lang/String;)Lnet/minecraftforge/common/config/Property;",
                    ordinal = 37))
    private static Property redirectLanguageHelper(Configuration instance, String category, String key,
            boolean defaultValue, String comment) {
        return LOTRConfig.config.get(
                CATEGORY_MISC,
                "Run language update helper",
                false,
                "Run the mod's language file update helper on launch - see .minecraft/mods/LOTR_UpdatedLangFiles/readme.txt");
    }
}
