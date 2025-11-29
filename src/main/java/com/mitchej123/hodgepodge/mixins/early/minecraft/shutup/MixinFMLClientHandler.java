package com.mitchej123.hodgepodge.mixins.early.minecraft.shutup;

import java.util.Map;
import java.util.Set;

import net.minecraft.util.ResourceLocation;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.google.common.base.Strings;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;
import com.mitchej123.hodgepodge.config.FixesConfig;

import cpw.mods.fml.client.FMLClientHandler;

@Mixin(value = FMLClientHandler.class, remap = false)
public class MixinFMLClientHandler {

    @Shadow
    private SetMultimap<String, ResourceLocation> missingTextures;
    @Shadow
    private Set<String> badTextureDomains;
    @Shadow
    private Table<String, String, Set<ResourceLocation>> brokenTextures;

    @Inject(method = "logMissingTextureErrors", at = @At("HEAD"), cancellable = true)
    private void shutup(CallbackInfo ci) {
        ci.cancel();
        if (missingTextures.isEmpty() && brokenTextures.isEmpty()) {
            return;
        }
        int missingCount = 0;
        int brokenCount = 0;
        for (String resourceDomain : missingTextures.keySet()) {
            missingCount += missingTextures.get(resourceDomain).size();
            if (brokenTextures.containsRow(resourceDomain)) {
                Map<String, Set<ResourceLocation>> resourceErrs = brokenTextures.row(resourceDomain);
                for (String error : resourceErrs.keySet()) {
                    brokenCount += resourceErrs.get(error).size();
                }
            }
        }
        Logger logger = LogManager.getLogger("TEXTURE ERRORS");
        logger.error(Strings.repeat("+=", 25));
        logger.error(
                "{} missing textures and {} broken textures were found. Enable TweaksConfig.hideTextureErrors in the Hodgepodge config for more info.",
                missingCount,
                brokenCount);
        logger.error(Strings.repeat("+=", 25));
        if (FixesConfig.enableMemoryFixes) {
            missingTextures = HashMultimap.create();
            badTextureDomains = Sets.newHashSet();
            brokenTextures = HashBasedTable.create();
        }
    }
}
