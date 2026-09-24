package com.mitchej123.hodgepodge.net;

import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.EnumDifficulty;

import com.gtnewhorizon.gtnhlib.network.base.IPacket;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class MessageChangeDifficulty implements IPacket {

    private EnumDifficulty difficulty;

    public MessageChangeDifficulty() {}

    public MessageChangeDifficulty(EnumDifficulty difficulty) {
        this.difficulty = difficulty;
    }

    @Override
    public void encode(PacketBuffer buf) {
        buf.writeByte(this.difficulty.getDifficultyId());
    }

    @Override
    public void decode(PacketBuffer buf) {
        this.difficulty = EnumDifficulty.getDifficultyEnum(buf.readUnsignedByte());
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IPacket executeClient(NetHandlerPlayClient handler) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.theWorld != null) mc.theWorld.difficultySetting = this.difficulty;
        return null;
    }
}
