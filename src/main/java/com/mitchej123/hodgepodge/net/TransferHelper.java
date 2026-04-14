package com.mitchej123.hodgepodge.net;

import net.minecraft.entity.player.EntityPlayerMP;

/**
 * Public API for triggering server-to-server transfers. Sends a transfer packet to the client, which will disconnect
 * from the current server and connect to the specified host:port.
 */
public class TransferHelper {

    /**
     * Transfer a player to another server.
     *
     * @param player the player to transfer
     * @param host   target server hostname or IP
     * @param port   target server port (1-65535)
     */
    public static void transfer(EntityPlayerMP player, String host, int port) {
        NetworkHandler.instance.sendTo(new MessageTransfer(host, port), player);
    }
}
