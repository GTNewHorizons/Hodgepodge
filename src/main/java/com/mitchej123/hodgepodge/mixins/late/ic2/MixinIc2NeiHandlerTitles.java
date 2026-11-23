package com.mitchej123.hodgepodge.mixins.late.ic2;

import net.minecraft.util.StatCollector;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import ic2.neiIntegration.core.recipehandler.AdvRecipeHandler;
import ic2.neiIntegration.core.recipehandler.FluidCannerRecipeHandler;
import ic2.neiIntegration.core.recipehandler.ScrapboxRecipeHandler;
import ic2.neiIntegration.core.recipehandler.SolidCannerRecipeHandler;

@Mixin(
        value = { AdvRecipeHandler.class, FluidCannerRecipeHandler.class, ScrapboxRecipeHandler.class,
                SolidCannerRecipeHandler.class },
        remap = false)
public class MixinIc2NeiHandlerTitles {

    @ModifyReturnValue(method = "getRecipeName", at = @At("RETURN"))
    private String hodgepodge$localizeRecipeName(String original) {
        return StatCollector.translateToLocal("hodgepodge.nei.title." + getClass().getSimpleName());
    }
}
