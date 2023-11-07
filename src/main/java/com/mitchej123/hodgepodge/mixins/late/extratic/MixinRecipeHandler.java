package com.mitchej123.hodgepodge.mixins.late.extratic;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import cpw.mods.fml.common.FMLLog;
import glassmaker.extratic.common.RecipeHandler;

@Mixin(RecipeHandler.class)
public class MixinRecipeHandler {

    /**
     * @author DrParadox
     * @reason Breaks Thermal Foundation's TConstruct material integration
     */
    @Redirect(
            method = "addPartsRecipes",
            at = @At(value = "INVOKE", target = "Lglassmaker/extratic/common/RecipeHandler;_addM3PreciousRecipes()V"),
            remap = false)
    private static void hodgepodge$disableM3PreciousRecipes() {
        FMLLog.info("Disabled ExtraTiC Recipe integration with Metallurgy 3 Precious Materials");
    }

    /**
     * @author DrParadox
     * @reason Breaks Thermal Foundation's TConstruct parts integration
     */
    @Redirect(
            method = "addPartsRecipes",
            at = @At(
                    value = "INVOKE",
                    target = "Lglassmaker/extratic/common/RecipeHandler;_addM3PreciousAlloyMixing()V"),
            remap = false)
    private static void hodgepodge$disableM3PreciousAlloyMixing() {
        FMLLog.info("Disabled ExtraTiC Alloy integration with Metallurgy 3 Precious Materials");
    }
}
