package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.network.play.client.C12PacketUpdateSign;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import com.mitchej123.hodgepodge.util.SignLimits;

/**
 * Vanilla {@code C12PacketUpdateSign.readPacketData} calls {@code readStringFromBuffer(15)} per line, which throws if
 * the incoming line is longer than 15 raw chars. With {@code &} color codes, a legitimate line can exceed that (e.g.
 * {@code &g&#FF0000&#0000FFhello} is 23 raw but 5 visible). Raise the read cap to {@link SignLimits#RAW} to match the
 * safety cap in {@link MixinNetHandlerPlayServer_SignLimit}; visible-char validation in {@code processUpdateSign} still
 * enforces the {@link SignLimits#VISIBLE}-visible limit.
 */
@Mixin(C12PacketUpdateSign.class)
public class MixinC12PacketUpdateSign_RaiseReadLimit {

    @ModifyConstant(method = "readPacketData", constant = @Constant(intValue = 15))
    private int hodgepodge$raisePacketReadLimit(int original) {
        return SignLimits.RAW;
    }
}
