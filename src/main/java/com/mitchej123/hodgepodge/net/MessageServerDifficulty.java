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
    public boolean locked;

    public MessageServerDifficulty() {}

    public MessageServerDifficulty(EnumDifficulty difficulty, boolean locked) {
        this.difficulty = difficulty;
        this.locked = locked;
    }

    @Override
    public void encode(PacketBuffer buf) {
        buf.writeByte(difficulty.getDifficultyId());
        buf.writeBoolean(locked);
    }

    @Override
    public void decode(PacketBuffer buf) {
        difficulty = EnumDifficulty.getDifficultyEnum(buf.readUnsignedByte());
        locked = buf.readBoolean();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IPacket executeClient(NetHandlerPlayClient handler) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.theWorld == null) return null;

        mc.theWorld.difficultySetting = difficulty;
        mc.gameSettings.difficulty = difficulty;

        // sync the client with the server
        IWorldDifficulty pwd = (IWorldDifficulty) mc.theWorld.getWorldInfo();
        pwd.setDifficulty(difficulty);
        pwd.setDifficultyLocked(locked);

        return null;
    }
}
