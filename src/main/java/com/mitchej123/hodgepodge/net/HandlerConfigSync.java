package com.mitchej123.hodgepodge.net;

import com.mitchej123.hodgepodge.Common;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class HandlerConfigSync implements IMessageHandler<MessageConfigSync, IMessage> {

    @Override
    public IMessage onMessage(MessageConfigSync message, MessageContext ctx) {
        Common.config.longerSentMessages = message.isLongerSentMessages();

        return null;
    }
}
