package com.mitchej123.hodgepodge.mixins.late.galacticraftcore;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import micdoodle8.mods.galacticraft.core.client.gui.container.GuiExtendedInventory;

@Mixin(value = GuiExtendedInventory.class)
public class MixinGuiExtendedInventory {

    @Redirect(
            method = "initGui",
            at = @At(
                    value = "INVOKE",
                    target = "Lmicdoodle8/mods/galacticraft/core/client/gui/container/GuiExtendedInventory;getPotionOffset()I",
                    remap = false))
    public int hodgepodge$fixPotionOffset1(GuiExtendedInventory instance) {
        return 0;
    }

    @Redirect(
            method = "initGui",
            at = @At(
                    value = "INVOKE",
                    target = "Lmicdoodle8/mods/galacticraft/core/client/gui/container/GuiExtendedInventory;getPotionOffsetNEI()I",
                    remap = false))
    public int hodgepodge$fixPotionOffset2(GuiExtendedInventory instance) {
        return 0;
    }
}
