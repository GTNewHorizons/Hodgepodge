package com.mitchej123.hodgepodge.mixins.late.extrautilities;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import com.mitchej123.hodgepodge.config.TweaksConfig;
import com.rwtema.extrautils.tileentity.enderquarry.TileEntityEnderQuarry;

@Mixin(value = TileEntityEnderQuarry.class, remap = false)
public class MixinTileEntityEnderQuarry {

    @ModifyArg(method = "<init>", at = @At(value = "INVOKE", target = "Lcofh/api/energy/EnergyStorage;<init>(I)V"))
    private int mixinInit(int energyStored) {
        return TweaksConfig.extraUtilitiesEnderQuarryOverride;
    }
}
