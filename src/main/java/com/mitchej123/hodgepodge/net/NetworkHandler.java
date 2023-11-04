package com.mitchej123.hodgepodge.net;

import com.mitchej123.hodgepodge.Hodgepodge;

import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;

public class NetworkHandler {

    public static final SimpleNetworkWrapper instance = new SimpleNetworkWrapper(Hodgepodge.MODID);

    public static void init() {
        instance.registerMessage(HandlerConfigSync.class, MessageConfigSync.class, 0, Side.CLIENT);
    }
}
