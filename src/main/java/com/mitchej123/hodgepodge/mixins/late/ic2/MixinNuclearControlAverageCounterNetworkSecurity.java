package com.mitchej123.hodgepodge.mixins.late.ic2;

import net.minecraft.entity.player.EntityPlayer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "shedar.mods.ic2.nuclearcontrol.tileentities.TileEntityAverageCounter", remap = false)
public class MixinNuclearControlAverageCounterNetworkSecurity {

    @Inject(method = "onNetworkEvent", at = @At("HEAD"), cancellable = true, remap = false)
    private void hodgepodge$validatePeriod(EntityPlayer player, int event, CallbackInfo callback) {
        // 0 resets the counter; the GUI only offers 1, 3, 5, and 10-second averages.
        if (event != 0 && event != 1 && event != 3 && event != 5 && event != 10) {
            callback.cancel();
        }
    }
}
