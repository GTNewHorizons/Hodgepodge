package com.mitchej123.hodgepodge.net;

import com.mitchej123.hodgepodge.LoadingConfig;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;

public class MessageConfigSync implements IMessage {

    private boolean longerSentMessages;

    public MessageConfigSync() {}

    public MessageConfigSync(LoadingConfig config) {
        longerSentMessages = config.longerSentMessages;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        longerSentMessages = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(longerSentMessages);
    }

    public boolean isLongerSentMessages() {
        return longerSentMessages;
    }
}
