package com.mitchej123.hodgepodge.client.chat;

import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

public class ChatComponentCount extends ChatComponentText {

    private final int count;

    public ChatComponentCount(int count) {
        super(EnumChatFormatting.GRAY + " (" + count + ")");
        this.count = count;
    }

    public int getCount() {
        return count;
    }

}
