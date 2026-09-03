package com.mitchej123.hodgepodge.mixins.interfaces;

import java.util.UUID;

import net.minecraft.network.NetworkManager;

import io.netty.channel.Channel;
import io.netty.util.AttributeKey;

/** Login bookkeeping stored on the channel so it survives every network-handler handoff. */
public final class LoginSessionState {

    private static final AttributeKey<UUID> ACCEPTED_UUID = new AttributeKey<>("hodgepodge:accepted_login_uuid");
    private static final AttributeKey<Integer> SUPERSEDED_AT = new AttributeKey<>("hodgepodge:login_superseded_at");
    private static final AttributeKey<Boolean> UNSAFE_STRANDED_RECOVERY = new AttributeKey<>(
            "hodgepodge:unsafe_stranded_recovery");
    private static final AttributeKey<Boolean> CLOSE_REQUESTED = new AttributeKey<>("hodgepodge:login_close_requested");
    private static final AttributeKey<Boolean> DISCONNECT_POSTED = new AttributeKey<>(
            "hodgepodge:login_disconnect_posted");

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
    public static boolean markSuperseded(NetworkManager manager, int tick) {
        return manager.channel().attr(SUPERSEDED_AT).setIfAbsent(tick) == null;
    }

    public static int getSupersededTick(NetworkManager manager) {
        return manager.channel().attr(SUPERSEDED_AT).get();
    }

    public static boolean isSuperseded(NetworkManager manager) {
        return manager.channel().attr(SUPERSEDED_AT).get() != null;
    }

    /** Retained after the competing session leaves, including across later login attempts. */
    public static void markStrandedRecoveryUnsafe(NetworkManager manager) {
        final Channel channel = manager.channel();
        if (channel != null) {
            channel.attr(UNSAFE_STRANDED_RECOVERY).set(Boolean.TRUE);
        }
    }

    public static boolean isStrandedRecoveryUnsafe(NetworkManager manager) {
        final Channel channel = manager.channel();
        return channel == null || Boolean.TRUE.equals(channel.attr(UNSAFE_STRANDED_RECOVERY).get());
    }

    /** Returns true only for the first FML disconnect event posted for this connection. */
    public static boolean markDisconnectPosted(NetworkManager manager) {
        return manager.channel().attr(DISCONNECT_POSTED).setIfAbsent(Boolean.TRUE) == null;
    }

    /** Returns true only for the first direct close request. */
    public static boolean requestClose(NetworkManager manager) {
        return manager.channel().attr(CLOSE_REQUESTED).setIfAbsent(Boolean.TRUE) == null;
    }
}
