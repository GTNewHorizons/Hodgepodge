package com.mitchej123.hodgepodge.mixins.late.extrautilities;

import net.minecraft.util.StatCollector;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import com.rwtema.extrautils.nei.EnderConstructorHandler;
import com.rwtema.extrautils.nei.FMPMicroBlocksHandler;
import com.rwtema.extrautils.nei.MicroBlocksHandler;

@Mixin(value = { EnderConstructorHandler.class, FMPMicroBlocksHandler.class, MicroBlocksHandler.class }, remap = false)
public class MixinExuNeiHandlerTitles {

    /**
     * @author shironakoushi
     * @reason Localize the hardcoded NEI recipe title
     */
    @Overwrite
    public String getRecipeName() {
        return StatCollector.translateToLocal("hodgepodge.nei.title." + getClass().getSimpleName());
    }
}
