package com.mitchej123.hodgepodge.mixins.interfaces;

import java.util.UUID;

import net.minecraft.network.NetworkManager;

import io.netty.channel.Channel;
import io.netty.util.AttributeKey;

/** Login bookkeeping stored on the channel so it survives every network-handler handoff. */
public final class LoginSessionState {

    private static final AttributeKey<UUID> ACCEPTED_UUID = new AttributeKey<>("hodgepodge:accepted_login_uuid");
    private static final AttributeKey<Boolean> SUPERSEDED = new AttributeKey<>("hodgepodge:superseded_login");
    private static final AttributeKey<Boolean> CLOSE_REQUESTED = new AttributeKey<>("hodgepodge:login_close_requested");

    private LoginSessionState() {}

    public static UUID getAcceptedUuid(NetworkManager manager) {
        // NetworkSystem registers a connection before its channel is set, so the scan can reach one without.
        final Channel channel = manager.channel();
        return channel == null ? null : channel.attr(ACCEPTED_UUID).get();
    }

    public static void setAcceptedUuid(NetworkManager manager, UUID uuid) {
        manager.channel().attr(ACCEPTED_UUID).set(uuid);
    }

    /** Returns true only for the login that first supersedes this connection. */
    public static boolean markSuperseded(NetworkManager manager) {
        return manager.channel().attr(SUPERSEDED).setIfAbsent(Boolean.TRUE) == null;
    }

    public static boolean isSuperseded(NetworkManager manager) {
        return Boolean.TRUE.equals(manager.channel().attr(SUPERSEDED).get());
    }

    /** Returns true only for the first direct close request. */
    public static boolean requestClose(NetworkManager manager) {
        return manager.channel().attr(CLOSE_REQUESTED).setIfAbsent(Boolean.TRUE) == null;
    }
}
