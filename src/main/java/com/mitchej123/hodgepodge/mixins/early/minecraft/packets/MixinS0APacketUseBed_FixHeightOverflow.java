package com.mitchej123.hodgepodge.mixins.early.minecraft.packets;

import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.S0APacketUseBed;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(S0APacketUseBed.class)
public class MixinS0APacketUseBed_FixHeightOverflow {

    @Shadow
    private int field_149096_c;

    @Inject(method = "readPacketData", at = @At("TAIL"))
    private void hodgepodge$fixBedHeight(PacketBuffer data, CallbackInfo ci) {
        this.field_149096_c &= 0xFF;
    }
}
