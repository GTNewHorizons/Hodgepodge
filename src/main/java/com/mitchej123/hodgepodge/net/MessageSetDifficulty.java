package com.mitchej123.hodgepodge.net;

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
    public void encode(PacketBuffer buf) {
        buf.writeByte(this.difficulty.getDifficultyId());
        buf.writeBoolean(this.locked);
    }

    @Override
    public void decode(PacketBuffer buf) {
        this.difficulty = EnumDifficulty.getDifficultyEnum(buf.readUnsignedByte());
        this.locked = buf.readBoolean();
    }

    @Override
    public IPacket executeServer(NetHandlerPlayServer handler) {
        final MinecraftServer server = MinecraftServer.getServer();
        if (server == null || !server.isSinglePlayer()
                || server.worldServers.length == 0
                || !server.getServerOwner().equals(handler.playerEntity.getCommandSenderName()))
            return null;

        WorldServer overworld = server.worldServers[0];
        if (overworld == null || !(overworld.getWorldInfo() instanceof IWorldDifficulty info)
                || info.hodgepodge$isDifficultyLocked()
                || overworld.getWorldInfo().isHardcoreModeEnabled())
            return null;

        if (this.locked) {
            info.hodgepodge$setDifficultyLocked(true);
            for (WorldServer world : server.worldServers) {
                if (world != null) {
                    NetworkHandler.instance.sendToDimension(
                            new MessageServerDifficulty(world.difficultySetting, info.hodgepodge$getDifficulty(), true),
                            world.provider.dimensionId);
                }
            }
        } else {
            server.func_147139_a(this.difficulty);
        }
        return null;
    }
}
