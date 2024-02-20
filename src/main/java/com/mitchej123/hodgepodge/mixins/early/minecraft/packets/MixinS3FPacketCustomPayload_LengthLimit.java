package com.mitchej123.hodgepodge.mixins.early.minecraft.packets;

import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S3FPacketCustomPayload;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.mitchej123.hodgepodge.config.FixesConfig;
import com.mitchej123.hodgepodge.mixins.early.minecraft.MixinMessageSerializer2;
import com.mitchej123.hodgepodge.util.PacketSerializationHelper;

import io.netty.buffer.ByteBuf;

@Mixin(S3FPacketCustomPayload.class)
public abstract class MixinS3FPacketCustomPayload_LengthLimit extends Packet {

    /**
     * @reason Customizable packet length limit. Requires {@link MixinMessageSerializer2}.
     */
    @ModifyConstant(method = "<init>(Ljava/lang/String;[B)V", constant = @Constant(intValue = 0x1FFF9A))
    private int hodgepodge$increasePacketSizeLimit(int original) {
        return FixesConfig.packetSizeLimit;
    }

    @ModifyConstant(
            method = "<init>(Ljava/lang/String;[B)V",
            constant = @Constant(stringValue = "Payload may not be larger than 2097050 bytes"))
    private String hodgepodge$increasePacketSizeLimitInErrorMsg(String original) {
        return "Payload may not be larger than " + FixesConfig.packetSizeLimit + " bytes (configurable in Hodgepodge)";
    }

    @Redirect(
            method = "readPacketData(Lnet/minecraft/network/PacketBuffer;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lcpw/mods/fml/common/network/ByteBufUtils;readVarShort(Lio/netty/buffer/ByteBuf;)I",
                    remap = false))
    private int hodgepodge$readLongerPacketData(ByteBuf buf) {
        return PacketSerializationHelper.readExtendedVarShortOrInt(buf);
    }

    @Redirect(
            method = "writePacketData(Lnet/minecraft/network/PacketBuffer;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lcpw/mods/fml/common/network/ByteBufUtils;writeVarShort(Lio/netty/buffer/ByteBuf;I)V",
                    remap = false))
    private void hodgepodge$writeLongerPacketData(ByteBuf buf, int toWrite) {
        PacketSerializationHelper.writeExtendedVarShortOrInt(buf, toWrite);
    }
}
