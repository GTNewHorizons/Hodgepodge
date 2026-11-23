package com.mitchej123.hodgepodge.mixins.late.extrautilities;

import net.minecraft.util.StatCollector;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;

@Mixin(value = com.rwtema.extrautils.nei.MicroBlocksHandler.class, remap = false)
public class MixinExuNeiMicroBlocksHandler {

    @ModifyReturnValue(method = "getRecipeName", at = @At("RETURN"))
    private String hodgepodge$localizeRecipeName(String original) {
        return StatCollector.translateToLocal("hodgepodge.nei.extrautilities.microblocks");
    }
}
