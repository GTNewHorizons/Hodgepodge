package com.mitchej123.hodgepodge.mixins.early.minecraft.packets;

import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.S01PacketJoinGame;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(S01PacketJoinGame.class)
public class MixinS01PacketJoinGame_FixDimensionID {

    @Shadow
    private int field_149202_d;

    @Inject(method = "writePacketData", at = @At("TAIL"))
    private void hodgepodge$writeDimensionID(PacketBuffer data, CallbackInfo ci) {
        data.writeInt(this.field_149202_d);
    }

    @Inject(method = "readPacketData", at = @At("TAIL"))
    private void hodgepodge$readDimensionID(PacketBuffer data, CallbackInfo ci) {
        this.field_149202_d = data.readInt();
    }
}
