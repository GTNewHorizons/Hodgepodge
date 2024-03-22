package com.mitchej123.hodgepodge.mixins.hooks;

import java.lang.ref.WeakReference;

import cpw.mods.fml.common.network.handshake.NetworkDispatcher;
import cpw.mods.fml.relauncher.Side;

/**
 * Forge doesn't properly set NetworkDispatcher objects on all of its networking objects, so we have to resort to
 * caching them externally.
 */
public final class NetworkDispatcherFallbackLookup {

    public static volatile WeakReference<NetworkDispatcher> CLIENT_DISPATCHER = new WeakReference<>(null);
    public static volatile WeakReference<NetworkDispatcher> SERVER_DISPATCHER = new WeakReference<>(null);

    private NetworkDispatcherFallbackLookup() {}

    public static NetworkDispatcher getFallbackDispatcher(Side side) {
        return switch (side) {
            case CLIENT -> CLIENT_DISPATCHER.get();
            case SERVER -> SERVER_DISPATCHER.get();
        };
    }
}
