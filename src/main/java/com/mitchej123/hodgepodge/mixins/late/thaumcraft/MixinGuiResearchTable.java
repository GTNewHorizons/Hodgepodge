package com.mitchej123.hodgepodge.mixins.late.thaumcraft;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.client.gui.GuiResearchTable;

import com.mitchej123.hodgepodge.util.AspectNameSorter;

@Mixin(GuiResearchTable.class)
public class MixinGuiResearchTable {

    @Redirect(
            at = @At(
                    remap = false,
                    target = "Lthaumcraft/api/aspects/AspectList;getAspectsSorted()[Lthaumcraft/api/aspects/Aspect;",
                    value = "INVOKE"),
            method = { "drawAspects", "drawAspectText", "drawScreen", "getClickedAspect" },
            remap = false)
    private Aspect[] hodgepodge$getAspectsSortedName(AspectList instance) {
        return AspectNameSorter.sort(instance);
    }

}
