package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.entity.EntityLivingBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityLivingBase.class)
public class MixinEntityLivingBase_FixHasteArmSwing {

    @Inject(method = "getArmSwingAnimationEnd", at = @At("RETURN"), cancellable = true)
    public void hodgepodge$fixHasteArmSwing(CallbackInfoReturnable<Integer> cir) {
        if (cir.getReturnValueI() < 2) {
            cir.setReturnValue(2);
        }
    }
}
