package com.mitchej123.hodgepodge.net;

import java.nio.charset.StandardCharsets;

import net.minecraft.util.IChatComponent;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;

public class MessageTitle implements IMessage {

    /** 0=TITLE, 1=SUBTITLE, 2=TIMES, 3=CLEAR, 4=RESET */
    public int action;

    /** For TITLE/SUBTITLE: the IChatComponent JSON */
    public String componentJson;

    /** For TIMES: fadeIn, stay, fadeOut ticks */
    public int fadeIn, stay, fadeOut;

    public MessageTitle() {}

    public static MessageTitle title(IChatComponent component) {
        MessageTitle msg = new MessageTitle();
        msg.action = 0;
        msg.componentJson = IChatComponent.Serializer.func_150696_a(component);
        return msg;
    }

    public static MessageTitle subtitle(IChatComponent component) {
        MessageTitle msg = new MessageTitle();
        msg.action = 1;
        msg.componentJson = IChatComponent.Serializer.func_150696_a(component);
        return msg;
    }

    public static MessageTitle times(int fadeIn, int stay, int fadeOut) {
        MessageTitle msg = new MessageTitle();
        msg.action = 2;
        msg.fadeIn = fadeIn;
        msg.stay = stay;
        msg.fadeOut = fadeOut;
        return msg;
    }

    public static MessageTitle clear() {
        MessageTitle msg = new MessageTitle();
        msg.action = 3;
        return msg;
    }

    public static MessageTitle reset() {
        MessageTitle msg = new MessageTitle();
        msg.action = 4;
        return msg;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        action = buf.readByte();
        switch (action) {
            case 0:
            case 1:
                int len = buf.readShort();
                byte[] bytes = new byte[len];
                buf.readBytes(bytes);
                componentJson = new String(bytes, StandardCharsets.UTF_8);
                break;
            case 2:
                fadeIn = buf.readInt();
                stay = buf.readInt();
                fadeOut = buf.readInt();
                break;
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeByte(action);
        switch (action) {
            case 0:
            case 1:
                byte[] bytes = componentJson.getBytes(StandardCharsets.UTF_8);
                buf.writeShort(bytes.length);
                buf.writeBytes(bytes);
                break;
            case 2:
                buf.writeInt(fadeIn);
                buf.writeInt(stay);
                buf.writeInt(fadeOut);
                break;
        }
    }
}
