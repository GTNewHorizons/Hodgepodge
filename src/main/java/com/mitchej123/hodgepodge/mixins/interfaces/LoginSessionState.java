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
    private static final AttributeKey<Boolean> PLAYER_SAVE_BLOCKED = new AttributeKey<>(
            "hodgepodge:player_save_blocked");
    private static final AttributeKey<Boolean> PLAYER_INSTALLED = new AttributeKey<>(
            "hodgepodge:login_player_installed");
    private static final AttributeKey<Boolean> PRE_WORLD_CLOSE = new AttributeKey<>("hodgepodge:login_pre_world_close");
    private static final AttributeKey<Integer> KICKED_AT = new AttributeKey<>("hodgepodge:login_kicked_at");
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

    /**
     * Only an observed stranded session is blocked; the live competitor must still write its final save.
     *
     * @return true only when this connection's player saves first become blocked
     */
    public static boolean blockPlayerSave(NetworkManager manager) {
        final Channel channel = manager.channel();
        return channel != null && channel.attr(PLAYER_SAVE_BLOCKED).setIfAbsent(Boolean.TRUE) == null;
    }

    public static boolean isPlayerSaveBlocked(NetworkManager manager) {
        final Channel channel = manager.channel();
        return channel == null || Boolean.TRUE.equals(channel.attr(PLAYER_SAVE_BLOCKED).get());
    }

    /** Retained across logout and respawn, even if a mod removes the player before closing the connection. */
    public static void markPlayerInstalled(NetworkManager manager) {
        final Channel channel = manager == null ? null : manager.channel();
        if (channel != null) {
            channel.attr(PLAYER_INSTALLED).set(Boolean.TRUE);
        }
    }

    /** Tags a forced close so logout can be skipped if this connection never installed a player. */
    public static void markPreWorldClose(NetworkManager manager) {
        manager.channel().attr(PRE_WORLD_CLOSE).set(Boolean.TRUE);
    }

    public static boolean isPreWorldClose(NetworkManager manager) {
        final Channel channel = manager.channel();
        return Boolean.TRUE.equals(channel.attr(PRE_WORLD_CLOSE).get())
                && !Boolean.TRUE.equals(channel.attr(PLAYER_INSTALLED).get());
    }

    /** Returns true only when this connection first becomes a live player after being superseded. */
    public static boolean markKicked(NetworkManager manager, int tick) {
        return manager.channel().attr(KICKED_AT).setIfAbsent(tick) == null;
    }

    public static int getKickedTick(NetworkManager manager) {
        return manager.channel().attr(KICKED_AT).get();
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
