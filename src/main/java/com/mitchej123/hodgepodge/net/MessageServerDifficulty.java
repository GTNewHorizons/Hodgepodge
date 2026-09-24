package com.mitchej123.hodgepodge.net;

import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.EnumDifficulty;

import com.gtnewhorizon.gtnhlib.network.base.IPacket;
import com.mitchej123.hodgepodge.mixins.interfaces.IWorldDifficulty;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class MessageServerDifficulty implements IPacket {

    public EnumDifficulty difficulty;
    public EnumDifficulty savedDifficulty;
    public boolean locked;

    public MessageServerDifficulty() {}

    public MessageServerDifficulty(EnumDifficulty difficulty, EnumDifficulty savedDifficulty, boolean locked) {
        this.difficulty = difficulty;
        this.savedDifficulty = savedDifficulty;
        this.locked = locked;
    }

    @Override
    public void encode(PacketBuffer buf) {
        buf.writeByte(difficulty.getDifficultyId());
        buf.writeByte(savedDifficulty.getDifficultyId());
        buf.writeBoolean(locked);
    }

    @Override
    public void decode(PacketBuffer buf) {
        difficulty = EnumDifficulty.getDifficultyEnum(buf.readUnsignedByte());
        savedDifficulty = EnumDifficulty.getDifficultyEnum(buf.readUnsignedByte());
        locked = buf.readBoolean();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IPacket executeClient(NetHandlerPlayClient handler) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.theWorld == null) return null;

        mc.theWorld.difficultySetting = difficulty;

        if (mc.theWorld.getWorldInfo() instanceof IWorldDifficulty worldInfo) {
            worldInfo.hodgepodge$setDifficulty(savedDifficulty);
            worldInfo.hodgepodge$setDifficultyLocked(locked);
        }

        return null;
    }
}
