package com.mitchej123.hodgepodge.mixins.late.extratic;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import cpw.mods.fml.common.FMLLog;
import glassmaker.extratic.common.PartsHandler;

@Mixin(PartsHandler.class)
public class MixinPartsHandler {

    /**
     * @author DrParadox
     * @reason Breaks TC integration with TE Materials
     */
    @Redirect(
            method = "addMatrials",
            at = @At(value = "INVOKE", target = "Lglassmaker/extratic/common/PartsHandler;_addM3PreciousMaterials()V"),
            remap = false)
    private static void _disableM3PreciousMaterials() {
        FMLLog.info("Disabled ExtraTiC material integration with Metallurgy 3 Precious Materials");
    }

    /**
     * @author DrParadox
     * @reason Breaks TC integration with TE Parts
     */
    @Redirect(
            method = "addParts",
            at = @At(value = "INVOKE", target = "Lglassmaker/extratic/common/PartsHandler;_addM3PreciousParts()V"),
            remap = false)
    private static void _disableM3PreciousParts() {
        FMLLog.info("Disabled ExtraTiC part integration with Metallurgy 3 Precious Materials");
    }
}
