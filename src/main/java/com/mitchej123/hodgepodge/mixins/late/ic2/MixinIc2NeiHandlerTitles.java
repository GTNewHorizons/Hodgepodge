package com.mitchej123.hodgepodge.mixins.late.ic2;

import net.minecraft.util.StatCollector;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import ic2.neiIntegration.core.recipehandler.AdvRecipeHandler;
import ic2.neiIntegration.core.recipehandler.FluidCannerRecipeHandler;
import ic2.neiIntegration.core.recipehandler.ScrapboxRecipeHandler;
import ic2.neiIntegration.core.recipehandler.SolidCannerRecipeHandler;

@Mixin(
        value = { AdvRecipeHandler.class, FluidCannerRecipeHandler.class, ScrapboxRecipeHandler.class,
                SolidCannerRecipeHandler.class },
        remap = false)
public class MixinIc2NeiHandlerTitles {

    /**
     * @author shironakoushi
     * @reason Localize the hardcoded NEI recipe title
     */
    @Overwrite
    public String getRecipeName() {
        return StatCollector.translateToLocal("hodgepodge.nei.title." + getClass().getSimpleName());
    }
}
