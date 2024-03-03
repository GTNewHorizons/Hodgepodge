package com.mitchej123.hodgepodge.mixins.late.ic2;

import net.minecraft.entity.EntityLivingBase;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import gregtech.api.util.GT_Utility;
import ic2.core.item.armor.ItemArmorHazmat;

@Mixin(value = ItemArmorHazmat.class, remap = false)
public class MixinIc2Hazmat {

    /**
     * @author Sphyix
     * @reason Hazmat - IC2 logic superseded by GT check
     */
    @Inject(at = @At("HEAD"), cancellable = true, method = "hasCompleteHazmat")
    private static void hodgepodge$checkGT(EntityLivingBase living, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(GT_Utility.isWearingFullRadioHazmat(living));
    }
}
