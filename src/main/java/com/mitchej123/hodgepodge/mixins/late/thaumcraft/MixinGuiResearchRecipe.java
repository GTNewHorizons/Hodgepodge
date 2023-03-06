package com.mitchej123.hodgepodge.mixins.late.thaumcraft;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.client.gui.GuiResearchRecipe;

import com.mitchej123.hodgepodge.util.AspectNameSorter;

@Mixin(GuiResearchRecipe.class)
public class MixinGuiResearchRecipe {

    @Redirect(
            at = @At(
                    remap = false,
                    target = "Lthaumcraft/api/aspects/AspectList;getAspectsSorted()[Lthaumcraft/api/aspects/Aspect;",
                    value = "INVOKE"),
            method = { "<init>", "drawAspectPage", "drawCruciblePage", "drawInfusionEnchantingPage",
                    "drawInfusionPage" },
            remap = false)
    private Aspect[] hodgepodge$getAspectsSortedName(AspectList instance) {
        return AspectNameSorter.sort(instance);
    }

}
