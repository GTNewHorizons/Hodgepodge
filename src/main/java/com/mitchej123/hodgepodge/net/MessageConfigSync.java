package com.mitchej123.hodgepodge.net;

import java.io.IOException;

import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.PacketBuffer;

import com.gtnewhorizon.gtnhlib.network.base.IPacket;
import com.mitchej123.hodgepodge.config.TweaksConfig;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class MessageConfigSync implements IPacket {

    private boolean longerSentMessages;
    private boolean fastBlockPlacingServerSide;

    public MessageConfigSync() {
        longerSentMessages = TweaksConfig.longerSentMessages;
        fastBlockPlacingServerSide = TweaksConfig.fastBlockPlacingServerSide;
    }

    @Override
    public void encode(PacketBuffer buf) throws IOException {
        buf.writeBoolean(longerSentMessages);
        buf.writeBoolean(fastBlockPlacingServerSide);
    }

    @Override
    public void decode(PacketBuffer buf) throws IOException {
        longerSentMessages = buf.readBoolean();
        // Ensures clients with the setting can still join servers without the setting
        // (servers running older versions of the mod).
        fastBlockPlacingServerSide = buf.readableBytes() < 1 || buf.readBoolean();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IPacket executeClient(NetHandlerPlayClient handler) {
        TweaksConfig.longerSentMessages = longerSentMessages;

        if (!fastBlockPlacingServerSide) {
            TweaksConfig.fastBlockPlacing = false;
            TweaksConfig.fastBlockPlacingServerSide = false;
        }

        return null;
    }
}
