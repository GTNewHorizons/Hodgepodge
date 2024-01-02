package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.util.MessageDeserializer2;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(MessageDeserializer2.class)
public class MixinMessageDeserializer2 {

    /**
     * @reason Remove the 21-bit (2MiB) limit of vanilla packet length. See {@link MixinMessageSerializer2}
     */
    @ModifyConstant(
            method = "decode(Lio/netty/channel/ChannelHandlerContext;Lio/netty/buffer/ByteBuf;Ljava/util/List;)V",
            constant = @Constant(intValue = 3))
    private int hodgepodge$increasePacketSizeLimit(int original) {
        return 5;
    }
}
