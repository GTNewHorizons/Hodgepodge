package com.mitchej123.hodgepodge.mixins.late.thaumcraft;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.mitchej123.hodgepodge.util.AspectNameSorter;

import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.client.gui.GuiThaumatorium;

@Mixin(GuiThaumatorium.class)
public class MixinGuiThaumatorium {

    @Redirect(
            at = @At(
                    remap = false,
                    target = "Lthaumcraft/api/aspects/AspectList;getAspectsSorted()[Lthaumcraft/api/aspects/Aspect;",
                    value = "INVOKE"),
            method = "drawAspects",
            remap = false)
    private Aspect[] hodgepodge$getAspectsSortedName(AspectList instance) {
        return AspectNameSorter.sort(instance);
    }

}
