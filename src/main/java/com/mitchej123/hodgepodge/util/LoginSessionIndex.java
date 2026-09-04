package com.mitchej123.hodgepodge.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.INetHandler;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.NetworkManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;

import com.mitchej123.hodgepodge.Common;
import com.mitchej123.hodgepodge.mixins.interfaces.LoginSessionState;

/**
 * Server-thread-only, lazy snapshot shared by all login waiters in one network tick. Full scans cost O(C + P), with
 * constant-time UUID lookup and shared blocker processing. Lifecycle changes refresh only the affected UUID bucket.
 */
public final class LoginSessionIndex {

    public interface Provider {

        LoginSessionIndex hodgepodge$getLoginSessionIndex();
    }

    private static final String KICK_REASON = "You logged in from another location";
    private static final int FORCE_CLOSE_AFTER = 5;

    private final MinecraftServer server;
    private final List<NetworkManager> managers;
    private final int tick;
    private final Set<NetworkManager> tracked = new HashSet<>();
    private final Map<NetworkManager, UUID> connectionUuids = new HashMap<>();
    private final Map<UUID, Sessions> byUuid = new HashMap<>();
    private boolean initialized;

    public LoginSessionIndex(MinecraftServer server, List<NetworkManager> managers, int networkTick) {
        this.server = server;
        this.managers = managers;
        // NetworkSystem replaces this snapshot at each network tick, even while the world is paused.
        this.tick = networkTick;
    }

    public Sessions getSessions(UUID uuid) {
        if (!initialized) {
            rebuild();
        }
        final Sessions sessions = bucket(uuid);
        sessions.prepare();
        return sessions;
    }

    private Sessions bucket(UUID uuid) {
        return byUuid.computeIfAbsent(uuid, ignored -> new Sessions());
    }

    private void rebuild() {
        tracked.clear();
        connectionUuids.clear();
        byUuid.clear();
        // networkTick already holds this monitor. Netty cannot register a connection during the snapshot.
        synchronized (managers) {
            for (NetworkManager manager : managers) {
                tracked.add(manager);
                final INetHandler handler = manager.getNetHandler();
                final EntityPlayerMP player = handler instanceof NetHandlerPlayServer
                        ? ((NetHandlerPlayServer) handler).playerEntity
                        : null;
                final UUID uuid = player == null ? LoginSessionState.getAcceptedUuid(manager) : player.getUniqueID();
                if (uuid != null) {
                    connectionUuids.put(manager, uuid);
                    bucket(uuid).connections.add(manager);
                }
            }
            for (EntityPlayerMP player : server.getConfigurationManager().playerEntityList) {
                bucket(player.getUniqueID()).players.add(player);
            }
        }
        initialized = true;
    }

    /** Reserve immediately: another login for this UUID may run later in the same networkTick. */
    public void accept(NetworkManager manager, UUID uuid) {
        LoginSessionState.setAcceptedUuid(manager, uuid);
        tracked.add(manager);
        connectionUuids.put(manager, uuid);
        final Sessions sessions = bucket(uuid);
        sessions.connections.add(manager);
        sessions.prepared = false;
    }

    /** Called at the player-list mutations, including the old/new entity swap during respawn. */
    public void playerAdded(EntityPlayerMP player) {
        // Record installation even before the first snapshot, and retain it after this index is discarded.
        final NetHandlerPlayServer handler = player.playerNetServerHandler;
        if (handler != null) {
            LoginSessionState.markPlayerInstalled(handler.func_147362_b());
        }
        if (initialized) {
            final Sessions sessions = bucket(player.getUniqueID());
            sessions.players.add(player);
            sessions.prepared = false;
        }
    }

    public void playerRemoved(EntityPlayerMP player) {
        if (initialized) {
            final Sessions sessions = bucket(player.getUniqueID());
            sessions.players.remove(player);
            sessions.prepared = false;
        }
    }

    /** Release only after onDisconnect has had its chance to save and remove the player. */
    public void connectionRemoved(NetworkManager manager) {
        if (initialized) {
            tracked.remove(manager);
            final UUID uuid = connectionUuids.remove(manager);
            if (uuid != null) {
                final Sessions sessions = bucket(uuid);
                sessions.connections.remove(manager);
                sessions.prepared = false;
            }
        }
    }

    public final class Sessions {

        private final Set<NetworkManager> connections = new HashSet<>();
        // Preserve duplicate list entries too: deduplicating a broken player list could hide an unsafe clone.
        private final List<EntityPlayerMP> players = new ArrayList<>();
        private final Set<NetworkManager> live = new HashSet<>();
        private final List<EntityPlayerMP> stranded = new ArrayList<>();
        private boolean prepared;
        private boolean repairAttempted;
        private int closingTicks;

        private void prepare() {
            if (prepared) {
                return;
            }
            prepared = true;
            live.clear();
            stranded.clear();
            final Set<EntityPlayerMP> installed = Collections.newSetFromMap(new IdentityHashMap<>());
            installed.addAll(players);
            for (NetworkManager manager : connections) {
                final INetHandler handler = manager.getNetHandler();
                if (handler instanceof NetHandlerPlayServer
                        && installed.contains(((NetHandlerPlayServer) handler).playerEntity)) {
                    live.add(manager);
                }
            }
            for (EntityPlayerMP player : players) {
                final NetHandlerPlayServer handler = player.playerNetServerHandler;
                if (handler == null || !tracked.contains(handler.func_147362_b())) {
                    stranded.add(player);
                }
            }

            // Remember ambiguous ownership even after a competing session saves and disappears.
            final boolean conflicting = connections.size() + stranded.size() > 1;
            if (conflicting) {
                connections.forEach(LoginSessionState::markStrandedRecoveryUnsafe);
            }
            for (EntityPlayerMP player : stranded) {
                if (player.playerNetServerHandler != null) {
                    final NetworkManager manager = player.playerNetServerHandler.func_147362_b();
                    if (conflicting) {
                        LoginSessionState.markStrandedRecoveryUnsafe(manager);
                    }
                    if (LoginSessionState.isStrandedRecoveryUnsafe(manager)
                            && LoginSessionState.blockPlayerSave(manager)) {
                        Common.log.warn(
                                "Blocking saves for {}'s stranded session because its save ownership is uncertain; manual cleanup may be required",
                                player.getCommandSenderName());
                    }
                }
            }

            closingTicks = Integer.MAX_VALUE;
            try {
                for (NetworkManager manager : connections) {
                    LoginSessionState.markSuperseded(manager, tick);
                    final boolean livePlayer = live.contains(manager);
                    if (livePlayer && LoginSessionState.markKicked(manager, tick)) {
                        ((NetHandlerPlayServer) manager.getNetHandler()).kickPlayerFromServer(KICK_REASON);
                    }
                    final int closingFor = tick - (livePlayer ? LoginSessionState.getKickedTick(manager)
                            : LoginSessionState.getSupersededTick(manager));
                    closingTicks = Math.min(closingTicks, closingFor);
                    // A missing player may be an unfinished handshake or a session already logged out by a mod.
                    // Installation history keeps the latter's final logout/save enabled.
                    if (closingFor >= FORCE_CLOSE_AFTER && manager.isChannelOpen()
                            && LoginSessionState.requestClose(manager)) {
                        if (!livePlayer) {
                            LoginSessionState.markPreWorldClose(manager);
                        }
                        manager.closeChannel(new ChatComponentText(KICK_REASON));
                    }
                }
            } catch (RuntimeException | Error e) {
                // A failed kick must not leave later waiters using an incomplete deadline calculation.
                prepared = false;
                throw e;
            }
        }

        public boolean isEmpty() {
            return connections.isEmpty() && stranded.isEmpty();
        }

        public boolean hasStranded() {
            return !stranded.isEmpty();
        }

        public int waited(int fallback) {
            return connections.isEmpty() ? fallback : closingTicks;
        }

        public void repairStranded() {
            if (!live.isEmpty() || repairAttempted) {
                return;
            }
            repairAttempted = true;
            // Logout listeners can reenter the barrier and refresh this bucket.
            for (EntityPlayerMP player : new ArrayList<>(stranded)) {
                final NetHandlerPlayServer handler = player.playerNetServerHandler;
                if (handler == null) {
                    Common.log.error(
                            "{} is in the world with no connection at all; they will not be able to log in until the server restarts",
                            player.getCommandSenderName());
                    continue;
                }
                if (LoginSessionState.isStrandedRecoveryUnsafe(handler.func_147362_b())) {
                    Common.log.error(
                            "Refusing to save the stranded session for {} because its save ownership is uncertain; manual cleanup is required",
                            player.getCommandSenderName());
                    continue;
                }
                Common.log.warn(
                        "{} is still in the world after their connection went away, so their disconnect never ran. Saving and removing them now so their next login attempt can succeed.",
                        player.getCommandSenderName());
                try {
                    handler.onDisconnect(new ChatComponentText(KICK_REASON));
                } catch (Throwable t) {
                    Common.log.error("Failed to clean up the stale session for {}", player.getCommandSenderName(), t);
                }
            }
        }
    }
}
