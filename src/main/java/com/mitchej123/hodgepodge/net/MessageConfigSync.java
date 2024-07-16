package com.mitchej123.hodgepodge.net;

import com.mitchej123.hodgepodge.config.TweaksConfig;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class MessageConfigSync implements IMessage, IMessageHandler<MessageConfigSync, IMessage> {

    private boolean longerSentMessages;
    private boolean fastBlockPlacingDisableServerSide;

    public MessageConfigSync() {
        longerSentMessages = TweaksConfig.longerSentMessages;
        fastBlockPlacingDisableServerSide = TweaksConfig.fastBlockPlacingDisableServerSide;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        longerSentMessages = buf.readBoolean();
        // Ensures clients with the setting can still join servers without the setting (servers running older versions
        // of the mod)
        if (buf.readableBytes() < 1) {
            fastBlockPlacingDisableServerSide = false;
        } else {
            fastBlockPlacingDisableServerSide = buf.readBoolean();
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(longerSentMessages);
        buf.writeBoolean(fastBlockPlacingDisableServerSide);
    }

    public boolean isFastBlockPlacingDisableServerSide() {
        return fastBlockPlacingDisableServerSide;
    }

    public boolean isLongerSentMessages() {
        return longerSentMessages;
    }

    @Override
    public IMessage onMessage(MessageConfigSync message, MessageContext ctx) {
        TweaksConfig.longerSentMessages = message.isLongerSentMessages();

        if (message.isFastBlockPlacingDisableServerSide()) {
            TweaksConfig.fastBlockPlacing = false;
            TweaksConfig.fastBlockPlacingDisableServerSide = true;
        }

        return null;
    }
}
