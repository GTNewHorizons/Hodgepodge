package com.mitchej123.hodgepodge.net;

import com.gtnewhorizon.gtnhlib.network.base.NetworkChannel;
import com.mitchej123.hodgepodge.Hodgepodge;
import com.mitchej123.hodgepodge.hax.TileEntityDescriptionBatcher.BatchedDescriptionPacket;

public class NetworkHandler {

    public static final NetworkChannel instance = new NetworkChannel(Hodgepodge.MODID);

    public static void init() {
        instance.toClient(new MessageConfigSync());
        instance.toClient(new BatchedDescriptionPacket());
        instance.toClient(new MessageServerDifficulty());
        instance.toServer(new MessageSetDifficulty());
    }
}
