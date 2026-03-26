package com.mitchej123.hodgepodge.mixins.hooks;

import java.util.Set;

import net.minecraft.command.CommandBase;

import com.mitchej123.hodgepodge.config.MemoryConfig;

import cofh.CoFHCore;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Optional;
import cpw.mods.fml.common.network.FMLEmbeddedChannel;
import cpw.mods.fml.common.network.FMLOutboundHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.handshake.NetworkDispatcher;
import cpw.mods.fml.relauncher.Side;

public final class AfterServerStoppedHook {

    /**
     * This gets called after all the FMLServerStoppedEvent have fired for all mods
     */
    public static void run() {
        if (MemoryConfig.leaks.fixCoFHWorldLeak && Loader.isModLoaded("CoFHCore")) {
            clearCoFhServerInstance();
        }
        if (MemoryConfig.leaks.fixNetworkChannelsMemoryLeak) {
            cleanChannelsForSide(Side.CLIENT);
            cleanChannelsForSide(Side.SERVER);
        }
        if (MemoryConfig.leaks.fixServerCommandHandlerLeak) {
            CommandBase.setAdminCommander(null);
        }
    }

    private static void cleanChannelsForSide(Side side) {
        final Set<String> channels = NetworkRegistry.INSTANCE.channelNamesFor(side);
        for (String name : channels) {
            final FMLEmbeddedChannel channel = NetworkRegistry.INSTANCE.getChannel(name, side);
            channel.attr(FMLOutboundHandler.FML_MESSAGETARGET).remove();
            channel.attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).remove();
            channel.attr(NetworkDispatcher.FML_DISPATCHER).remove();
            channel.attr(NetworkRegistry.NET_HANDLER).remove();
        }
    }

    @Optional.Method(modid = "CoFHCore")
    private static void clearCoFhServerInstance() {
        CoFHCore.server = null;
    }
}
