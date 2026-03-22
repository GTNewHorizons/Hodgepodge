package com.mitchej123.hodgepodge.net;

import com.mitchej123.hodgepodge.client.tab.TabChannelHandler;
import com.mitchej123.hodgepodge.config.TweaksConfig;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class MessageConfigSync implements IMessage, IMessageHandler<MessageConfigSync, IMessage> {

    private boolean longerSentMessages;
    private boolean fastBlockPlacingServerSide;
    private String tabHeaderText = "";
    private String tabFooterText = "";

    public MessageConfigSync() {
        longerSentMessages = TweaksConfig.longerSentMessages;
        fastBlockPlacingServerSide = TweaksConfig.fastBlockPlacingServerSide;
        tabHeaderText = TweaksConfig.tabHeaderText != null ? TweaksConfig.tabHeaderText : "";
        tabFooterText = TweaksConfig.tabFooterText != null ? TweaksConfig.tabFooterText : "";
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        longerSentMessages = buf.readBoolean();
        // Ensures clients with the setting can still join servers without the setting (servers running older versions
        // of the mod)
        if (buf.readableBytes() < 1) {
            fastBlockPlacingServerSide = true;
        } else {
            fastBlockPlacingServerSide = buf.readBoolean();
        }
        if (buf.readableBytes() > 0) {
            tabHeaderText = ByteBufUtils.readUTF8String(buf);
            tabFooterText = ByteBufUtils.readUTF8String(buf);
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(longerSentMessages);
        buf.writeBoolean(fastBlockPlacingServerSide);
        ByteBufUtils.writeUTF8String(buf, tabHeaderText);
        ByteBufUtils.writeUTF8String(buf, tabFooterText);
    }

    public boolean isFastBlockPlacingServerSide() {
        return fastBlockPlacingServerSide;
    }

    public boolean isLongerSentMessages() {
        return longerSentMessages;
    }

    @Override
    public IMessage onMessage(MessageConfigSync message, MessageContext ctx) {
        TweaksConfig.longerSentMessages = message.isLongerSentMessages();

        if (!message.isFastBlockPlacingServerSide()) {
            TweaksConfig.fastBlockPlacing = false;
            TweaksConfig.fastBlockPlacingServerSide = false;
        }

        TabChannelHandler.INSTANCE.setServerData(message.tabHeaderText, message.tabFooterText);

        return null;
    }
}
