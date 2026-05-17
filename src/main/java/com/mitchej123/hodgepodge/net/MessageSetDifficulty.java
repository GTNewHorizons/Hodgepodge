package com.mitchej123.hodgepodge.net;

import java.io.IOException;

import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.WorldServer;

import com.gtnewhorizon.gtnhlib.network.base.IPacket;
import com.mitchej123.hodgepodge.mixins.interfaces.IWorldDifficulty;

public class MessageSetDifficulty implements IPacket {

    private EnumDifficulty difficulty;
    private boolean locked;

    public MessageSetDifficulty() {}

    public MessageSetDifficulty(EnumDifficulty difficulty, boolean locked) {
        this.difficulty = difficulty;
        this.locked = locked;
    }

    @Override
    public void encode(PacketBuffer buf) throws IOException {
        buf.writeByte(this.difficulty.getDifficultyId());
        buf.writeBoolean(this.locked);
    }

    @Override
    public void decode(PacketBuffer buf) throws IOException {
        this.difficulty = EnumDifficulty.getDifficultyEnum(buf.readUnsignedByte());
        this.locked = buf.readBoolean();
    }

    @Override
    public IPacket executeServer(NetHandlerPlayServer handler) {
        final MinecraftServer server = MinecraftServer.getServer();
        if (server == null) return null;

        // send the difficulty changes from the client to the server
        // used for changing the difficulty from the options button
        server.func_147139_a(this.difficulty);

        if (this.locked) {
            for (WorldServer world : server.worldServers) {
                if (world != null && world.getWorldInfo() instanceof IWorldDifficulty worldDifficulty) {
                    worldDifficulty.setDifficultyLocked(true);
                }
            }
            for (WorldServer world : server.worldServers) {
                if (world != null && world.provider != null) {
                    NetworkHandler.instance.sendToDimension(
                            new MessageServerDifficulty(this.difficulty, true),
                            world.provider.dimensionId);
                }
            }
        }
        return null;
    }
}
