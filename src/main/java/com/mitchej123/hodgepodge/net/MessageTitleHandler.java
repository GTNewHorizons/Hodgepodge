package com.mitchej123.hodgepodge.net;

import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;

import com.gtnewhorizon.gtnhlib.client.title.TitleAPI;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class MessageTitleHandler implements IMessageHandler<MessageTitle, IMessage> {

    @Override
    public IMessage onMessage(MessageTitle msg, MessageContext ctx) {
        switch (msg.action) {
            case 0: // TITLE
                TitleAPI.setTitle(deserialize(msg.componentJson));
                break;
            case 1: // SUBTITLE
                TitleAPI.setSubtitle(deserialize(msg.componentJson));
                break;
            case 2: // TIMES
                TitleAPI.setTimes(msg.fadeIn, msg.stay, msg.fadeOut);
                break;
            case 3: // CLEAR
                TitleAPI.clear();
                break;
            case 4: // RESET
                TitleAPI.clear();
                TitleAPI.reset();
                break;
        }
        return null;
    }

    private static IChatComponent deserialize(String json) {
        if (json == null || json.isEmpty()) return new ChatComponentText("");
        try {
            return IChatComponent.Serializer.func_150699_a(json);
        } catch (Exception e) {
            return new ChatComponentText(json);
        }
    }
}
