package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.network.play.client.C01PacketChatMessage;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import com.mitchej123.hodgepodge.config.TweaksConfig;

@Mixin(C01PacketChatMessage.class)
public class MixinC01PacketChatMessage_LongerMessages {

    @ModifyConstant(method = { "<init>(Ljava/lang/String;)V", "readPacketData" }, constant = @Constant(intValue = 100))
    public int hodgepodge$longerMessages(int constant) {
        return TweaksConfig.longerSentMessages ? 256 : constant;
    }
}
