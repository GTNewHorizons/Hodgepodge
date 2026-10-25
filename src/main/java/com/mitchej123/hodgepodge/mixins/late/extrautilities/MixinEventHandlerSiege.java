package com.mitchej123.hodgepodge.mixins.late.extrautilities;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.rwtema.extrautils.EventHandlerSiege;

@Mixin(value = EventHandlerSiege.class, remap = false)
public class MixinEventHandlerSiege {

    @Redirect(
            method = "golemDeath",
            at = @At(value = "INVOKE", target = "Ljava/lang/Object;equals(Ljava/lang/Object;)Z"))
    private boolean checkSubclassofBeacon(Object subject, Object teBeaconClass) {
        return ((Class<?>) teBeaconClass).isAssignableFrom((Class<?>) subject);
    }
}
