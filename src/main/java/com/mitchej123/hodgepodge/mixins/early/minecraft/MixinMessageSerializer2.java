package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.util.MessageSerializer2;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(MessageSerializer2.class)
public class MixinMessageSerializer2 {

    /**
     * @reason Remove the 21-bit (2MiB) limit of vanilla packet length.
     */
    @ModifyConstant(
            method = "encode(Lio/netty/channel/ChannelHandlerContext;Lio/netty/buffer/ByteBuf;Lio/netty/buffer/ByteBuf;)V",
            constant = @Constant(intValue = 3))
    private int hodgepodge$increasePacketSizeLimit(int original) {
        // 32-bit length limit, which gets encoded as 5 7-bit varint bytes
        return 5;
    }
}
