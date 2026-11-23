package com.mitchej123.hodgepodge.mixins.late.extrautilities;

import net.minecraft.util.StatCollector;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.rwtema.extrautils.nei.EnderConstructorHandler;
import com.rwtema.extrautils.nei.FMPMicroBlocksHandler;
import com.rwtema.extrautils.nei.MicroBlocksHandler;

@Mixin(value = { EnderConstructorHandler.class, FMPMicroBlocksHandler.class, MicroBlocksHandler.class }, remap = false)
public class MixinExuNeiHandlerTitles {

    @ModifyReturnValue(method = "getRecipeName", at = @At("RETURN"))
    private String hodgepodge$localizeRecipeName(String original) {
        return StatCollector.translateToLocal("hodgepodge.nei.title." + getClass().getSimpleName());
    }
}
