package com.mitchej123.hodgepodge.mixins.early.fml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.google.common.base.Charsets;
import com.mitchej123.hodgepodge.Common;

import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.relauncher.Side;
import ic2.core.init.Localization;

@Mixin(LanguageRegistry.class)
public class MixinLanguageRegistry {

    /**
     * Loads `ic2/lang/xx_XX.properties` files as .lang files during mod construction instead of postinit
     */
    @Inject(method = "loadLanguagesFor", at = @At("HEAD"), cancellable = true, remap = false)
    private void hodgepodge$injectLanguagesFor(ModContainer container, Side side, CallbackInfo ci) {
        if ("IC2".equals(container.getModId())) {
            try {
                hodgepodge$loadLanguagesForIC2(container);
            } catch (IOException e) {
                Common.log.error("Failed to load IC2 translations.");
                Common.log.catching(e);
            }
            ci.cancel();
        }
    }

    /**
     * Adapted from {@link Localization#loadLocalizations}
     */
    @Unique
    private void hodgepodge$loadLanguagesForIC2(ModContainer container) throws IOException {
        File source = container.getSource();
        try (ZipFile zipFile = new ZipFile(source)) {
            for (ZipEntry entry : Collections.list(zipFile.entries())) {
                String name = entry.getName();
                if (!name.startsWith("ic2/lang/")) continue;

                String fileName = name.substring("ic2/lang/".length());
                if (!fileName.contains("/") && fileName.endsWith(".properties")) {
                    try (InputStream is = zipFile.getInputStream(entry)) {
                        hodgepodge$loadIC2Localization(is, fileName.split("\\.")[0]);
                    }
                }
            }

            Common.log.debug("Loaded IC2 translations from file {}.", source);
        }
    }

    /**
     * Adapted from {@link Localization#loadLocalization} and {@link LanguageRegistry#searchZipForLanguages}
     */
    @Unique
    private static void hodgepodge$loadIC2Localization(InputStream inputStream, String lang) throws IOException {
        Properties properties = new Properties();
        properties.load(new InputStreamReader(inputStream, Charsets.UTF_8));
        HashMap<String, String> map = new HashMap<>();

        for (Map.Entry<Object, Object> entries : properties.entrySet()) {
            Object key = entries.getKey();
            Object value = entries.getValue();
            if (key instanceof String stringKey && value instanceof String stringValue) {
                if (!stringKey.startsWith("achievement.") && !stringKey.startsWith("itemGroup.")
                        && !stringKey.startsWith("death.")) {
                    stringKey = "ic2." + stringKey;
                }
                map.put(stringKey, stringValue);
            }
        }

        LanguageRegistry.instance().injectLanguage(lang, map);
    }
}
