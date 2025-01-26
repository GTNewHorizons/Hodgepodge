package com.mitchej123.hodgepodge.mixins.late.mps;

import net.machinemuse.api.electricity.ElectricAdapter;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ElectricAdapter.class)
public class MixinElectricAdapterRF {

    @Redirect(
            method = "wrap",
            at = @At(value = "INVOKE", target = "Lnet/machinemuse/powersuits/common/ModCompatibility;isRFAPILoaded()Z"),
            remap = false)
    private static boolean hodgepodge$DisableInventorySynphonMPSforRF() {
        return false;
    }
}
