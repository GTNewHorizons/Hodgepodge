package com.mitchej123.hodgepodge.mixins.late.journeymap;

import net.minecraft.client.settings.KeyBinding;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import journeymap.client.Constants;

@Mixin(Constants.class)
public class MixinConstants {

    /**
     * @author Alexdoru
     * @reason Prevent unbound keybinds from triggering when pressing certain keys
     */
    @Inject(method = "isPressed", at = @At("HEAD"), remap = false, cancellable = true)
    private static void hodgepodge$isPressed(KeyBinding keyBinding, CallbackInfoReturnable<Boolean> cir) {
        if (keyBinding.getKeyCode() == 0) {
            cir.setReturnValue(false);
            cir.cancel();
        }
    }
}
