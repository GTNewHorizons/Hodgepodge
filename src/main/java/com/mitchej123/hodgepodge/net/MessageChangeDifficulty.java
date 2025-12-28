package com.mitchej123.hodgepodge.net;

import net.minecraft.client.Minecraft;
import net.minecraft.world.EnumDifficulty;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class MessageChangeDifficulty implements IMessage, IMessageHandler<MessageChangeDifficulty, IMessage> {

    public EnumDifficulty difficulty;

    public MessageChangeDifficulty(EnumDifficulty difficulty) {
        this.difficulty = difficulty;
    }

    public MessageChangeDifficulty() {}

    @Override
    public void fromBytes(ByteBuf buf) {
        this.difficulty = EnumDifficulty.getDifficultyEnum(buf.readByte());
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeByte(this.difficulty.getDifficultyId());
    }

    @Override
    public IMessage onMessage(MessageChangeDifficulty message, MessageContext ctx) {
        Minecraft.getMinecraft().theWorld.difficultySetting = message.difficulty;

        return null;
    }
}
