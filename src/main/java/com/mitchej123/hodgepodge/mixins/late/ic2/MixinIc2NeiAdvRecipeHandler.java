package com.mitchej123.hodgepodge.mixins.late.ic2;

import net.minecraft.util.StatCollector;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;

@Mixin(value = ic2.neiIntegration.core.recipehandler.AdvRecipeHandler.class, remap = false)
public class MixinIc2NeiAdvRecipeHandler {

    @ModifyReturnValue(method = "getRecipeName", at = @At("RETURN"))
    private String hodgepodge$localizeRecipeName(String original) {
        return StatCollector.translateToLocal("hodgepodge.nei.ic2.advcrafting");
    }
}
