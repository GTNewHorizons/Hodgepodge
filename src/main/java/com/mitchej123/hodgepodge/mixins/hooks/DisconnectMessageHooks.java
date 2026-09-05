package com.mitchej123.hodgepodge.mixins.hooks;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;

/**
 * Vanilla disconnects players with hardcoded English strings wrapped in a ChatComponentText, so they always show up in
 * English no matter what language the client is running. Map those strings back to translation keys, which the client
 * resolves in its own language.
 */
public class DisconnectMessageHooks {

    private static final Map<String, String> KEYS = new HashMap<>();

    static {
        // NetHandlerPlayServer
        KEYS.put("You have been idle for too long!", "hodgepodge.disconnect.idle");
        KEYS.put("Illegal stance", "hodgepodge.disconnect.illegal_stance");
        KEYS.put("Illegal position", "hodgepodge.disconnect.illegal_position");
        KEYS.put("Flying is not enabled on this server", "hodgepodge.disconnect.flying");
        KEYS.put("Illegal characters in chat", "hodgepodge.disconnect.illegal_characters");
        KEYS.put("Attempting to attack an invalid entity", "hodgepodge.disconnect.invalid_entity");
        KEYS.put("You have died. Game over, man, it's game over!", "hodgepodge.disconnect.hardcore_death");
        // Vanilla passes this key as plain text, so the player is shown the raw key instead of the message
        KEYS.put("disconnect.spam", "disconnect.spam");
        // ServerConfigurationManager
        KEYS.put("Server closed", "hodgepodge.disconnect.server_shutdown");
        KEYS.put("You logged in from another location", "hodgepodge.disconnect.logged_in_elsewhere");
        KEYS.put("You are not white-listed on this server!", "hodgepodge.disconnect.not_whitelisted");
        KEYS.put("The server is full!", "hodgepodge.disconnect.server_full");
        // IntegratedPlayerList
        KEYS.put("That name is already taken.", "hodgepodge.disconnect.name_taken");
        // NetHandlerLoginServer
        KEYS.put("Took too long to log in", "hodgepodge.disconnect.login_timeout");
        // MixinNetHandlerLoginServer_AwaitPreviousSession
        KEYS.put(
                "Your previous session is still being cleaned up, please reconnect in a moment",
                "hodgepodge.disconnect.session_cleanup_pending");
        KEYS.put(
                "Your previous session could not be cleaned up safely. Please contact a server administrator.",
                "hodgepodge.disconnect.session_cleanup_failed");
        // CommandBanPlayer / CommandBanIp
        KEYS.put("You are banned from this server.", "hodgepodge.disconnect.banned");
        KEYS.put("You have been IP banned.", "hodgepodge.disconnect.ip_banned");
    }

    /**
     * @return a translated component for a known vanilla disconnect message, or null if the reason is not one of them
     */
    public static IChatComponent localize(String reason) {
        final String key = KEYS.get(reason);
        return key == null ? null : new ChatComponentTranslation(key);
    }
}
