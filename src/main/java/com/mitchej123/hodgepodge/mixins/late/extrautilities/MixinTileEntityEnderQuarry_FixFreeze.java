package com.mitchej123.hodgepodge.mixins.late.extrautilities;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.rwtema.extrautils.tileentity.enderquarry.TileEntityEnderQuarry;

@Mixin(TileEntityEnderQuarry.class)
public class MixinTileEntityEnderQuarry_FixFreeze {

    @ModifyReturnValue(method = "harvestBlock", at = @At("RETURN"), remap = false)
    private boolean hodgepodge$fixFreeze(boolean original) {
        // To fix a weird issue with certain mods that let the Ender Quarry stuck at a
        // mostly random location, return true also in the following (all) scenario:
        // - The harvested block has no drops
        // - The harvested block has unexpected drops
        // - The harvested block has been harvested by another Entity
        return true;
    }
}
