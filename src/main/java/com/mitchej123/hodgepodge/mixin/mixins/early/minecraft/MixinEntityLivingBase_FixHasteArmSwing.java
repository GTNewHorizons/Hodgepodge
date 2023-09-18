package com.mitchej123.hodgepodge.mixin.mixins.early.minecraft;

import net.minecraft.entity.EntityLivingBase;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.gtnewhorizon.mixinextras.injector.ModifyReturnValue;

@Mixin(EntityLivingBase.class)
public class MixinEntityLivingBase_FixHasteArmSwing {

    @ModifyReturnValue(method = "getArmSwingAnimationEnd", at = @At("RETURN"))
    private int hodgepodge$fixHasteArmSwing(int original) {
        return Math.max(original, 2);
    }
}
