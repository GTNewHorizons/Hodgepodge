package com.mitchej123.hodgepodge.mixin.mixins.late.thaumcraft;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.mitchej123.hodgepodge.util.AspectNameSorter;

import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.client.gui.GuiFocalManipulator;

@Mixin(GuiFocalManipulator.class)
public class MixinGuiFocalManipulator {

    @Redirect(
            at = @At(
                    remap = false,
                    target = "Lthaumcraft/api/aspects/AspectList;getAspectsSorted()[Lthaumcraft/api/aspects/Aspect;",
                    value = "INVOKE"),
            method = "drawGuiContainerBackgroundLayer")
    private Aspect[] hodgepodge$getAspectsSortedName(AspectList instance) {
        return AspectNameSorter.sort(instance);
    }

}
