package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.tileentity.TileEntityPiston;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TileEntityPiston.class)
public class MixinTileEntityPiston {

    @Inject(method = "getPistonOrientation", at = @At("RETURN"), cancellable = true)
    private void hodgepodge$clampOrientation(CallbackInfoReturnable<Integer> cir) {
        int orientation = cir.getReturnValue();

        if (orientation < 0 || orientation >= 6) {
            cir.setReturnValue(orientation % 6);
        }
    }
}
