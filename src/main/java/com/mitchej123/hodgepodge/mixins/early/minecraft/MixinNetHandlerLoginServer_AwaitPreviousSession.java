package com.mitchej123.hodgepodge.mixins.early.minecraft;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.INetHandler;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.NetworkManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraft.server.network.NetHandlerLoginServer;
import net.minecraft.util.ChatComponentText;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mitchej123.hodgepodge.Common;
import com.mitchej123.hodgepodge.mixins.interfaces.LoginSessionState;
import com.mojang.authlib.GameProfile;

/**
 * Holds a login back while another session for the same UUID is still on its way out of the world.
 * <p>
 * Vanilla kicks the previous session from createPlayerForUser and carries on immediately, but that kick only schedules
 * a disconnect packet, so the old session's save and removal do not happen until a later networkTick sees the closed
 * channel. The replacement reads a stale player file while the old entity is still alive holding the same inventory.
 * Vanilla also only checks playerEntityList, which a session does not join until the FML handshake ends, so a second
 * login arriving during the handshake is not noticed at all.
 * <p>
 * onNetworkTick retries func_147326_c every tick and nothing is sent to the client until it completes, so cancelling it
 * before vanilla marks the login accepted is a free retry: kick once, then wait for networkTick to do the cleanup on
 * its usual code path. Both the kick and the cleanup run on the server thread, so their ordering is guaranteed rather
 * than raced for.
 */
@Mixin(NetHandlerLoginServer.class)
public abstract class MixinNetHandlerLoginServer_AwaitPreviousSession {

    @Unique
    private static final String hodgepodge$KICK_REASON = "You logged in from another location";

    @Unique
    private static final int hodgepodge$FORCE_CLOSE_AFTER = 5;

    /** Cleanup normally lands on the next tick, so reaching this means it is never coming. */
    @Unique
    private static final int hodgepodge$GIVE_UP_AFTER = 60;

    @Unique
    private int hodgepodge$ticksWaited;

    @Unique
    private boolean hodgepodge$gaveUp;

    @Shadow
    private GameProfile field_147337_i;

    @Shadow
    @Final
    public NetworkManager field_147333_a;

    @Shadow
    public abstract void func_147322_a(String reason);

    @Inject(
            method = "func_147326_c",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/server/network/NetHandlerLoginServer;field_147328_g:Lnet/minecraft/server/network/NetHandlerLoginServer$LoginState;",
                    opcode = Opcodes.PUTFIELD),
            cancellable = true)
    private void hodgepodge$awaitPreviousSession(CallbackInfo ci) {
        final MinecraftServer server = MinecraftServer.getServer();
        if (server == null || this.field_147337_i == null) {
            return;
        }
        final ServerConfigurationManager scm = server.getConfigurationManager();
        if (scm == null) {
            return;
        }

        if (this.hodgepodge$gaveUp) {
            ci.cancel();
            return;
        }

        // Vanilla has completed and admitted this profile before reaching the ACCEPTED assignment.
        final UUID uuid = EntityPlayer.func_146094_a(this.field_147337_i);

        final List<NetworkManager> live = new ArrayList<>();
        final List<EntityPlayerMP> stranded = new ArrayList<>();
        final List<NetworkManager> accepting = new ArrayList<>();

        if (!this.hodgepodge$collectSessions(server, scm, uuid, live, stranded, accepting)) {
            this.hodgepodge$ticksWaited = 0;
            LoginSessionState.setAcceptedUuid(this.field_147333_a, uuid);
            return;
        }

        final int waited = this.hodgepodge$ticksWaited++;

        for (NetworkManager manager : live) {
            if (LoginSessionState.markSuperseded(manager)) {
                ((NetHandlerPlayServer) manager.getNetHandler()).kickPlayerFromServer(hodgepodge$KICK_REASON);
            } else if (waited >= hodgepodge$FORCE_CLOSE_AFTER) {
                // kickPlayerFromServer closes only once the disconnect packet has been written, which on a half-dead
                // connection means waiting out the OS retransmit timeout - exactly the case this fix exists for.
                this.hodgepodge$forceClose(manager);
            }
        }

        if (waited >= hodgepodge$GIVE_UP_AFTER) {
            for (EntityPlayerMP player : stranded) {
                this.hodgepodge$repairStrandedSession(player);
            }
            if (!this.hodgepodge$collectSessions(server, scm, uuid, live, stranded, accepting)) {
                // The repair freed the last blocker, so there is nothing left to wait for.
                this.hodgepodge$ticksWaited = 0;
                LoginSessionState.setAcceptedUuid(this.field_147333_a, uuid);
                return;
            }
            // func_147322_a leaves the login state alone, so onNetworkTick keeps calling us until the channel is
            // actually seen closed a tick or two later.
            this.hodgepodge$gaveUp = true;
            this.func_147322_a("Your previous session is still being cleaned up, please reconnect in a moment");
        }

        ci.cancel();
    }

    /** Closes a connection at most once, however many ticks ask for it. */
    @Unique
    private void hodgepodge$forceClose(NetworkManager manager) {
        if (manager.isChannelOpen() && LoginSessionState.requestClose(manager)) {
            manager.closeChannel(new ChatComponentText(hodgepodge$KICK_REASON));
        }
    }

    /** Returns true while something still holds this UUID. */
    @Unique
    private boolean hodgepodge$collectSessions(MinecraftServer server, ServerConfigurationManager scm, UUID uuid,
            List<NetworkManager> live, List<EntityPlayerMP> stranded, List<NetworkManager> accepting) {
        live.clear();
        stranded.clear();
        accepting.clear();
        final List<?> managers = ((NetworkSystemAccessor) server.func_147137_ag()).hodgepodge$getNetworkManagers();

        // networkTick, our caller, already holds this monitor; taking it again is reentrant and keeps Netty threads
        // from registering connections underneath us.
        synchronized (managers) {
            for (Object entry : managers) {
                final NetworkManager manager = (NetworkManager) entry;
                if (manager == this.field_147333_a) {
                    continue;
                }
                final INetHandler handler = manager.getNetHandler();
                if (handler instanceof NetHandlerPlayServer) {
                    final EntityPlayerMP player = ((NetHandlerPlayServer) handler).playerEntity;
                    if (player != null && uuid.equals(player.getUniqueID())) {
                        // Only a session that reached the world is kicked. FML builds NetHandlerPlayServer before
                        // its client round trips, so closing one still arriving logs out a session that never
                        // logged in; those are waited out instead.
                        if (scm.playerEntityList.contains(player)) {
                            live.add(manager);
                        } else {
                            accepting.add(manager);
                        }
                        continue;
                    }
                }

                // The accepted UUID remains on the connection while FML replaces the login handler and constructs the
                // play handler, including the brief window where that handler has no playerEntity yet.
                if (uuid.equals(LoginSessionState.getAcceptedUuid(manager))) {
                    accepting.add(manager);
                }
            }

            // Nothing ever sweeps playerEntityList, and networkTick only reaches a player through their
            // NetworkManager, so a player listed without one would block this UUID forever.
            for (int i = 0; i < scm.playerEntityList.size(); i++) {
                final EntityPlayerMP player = scm.playerEntityList.get(i);
                if (!uuid.equals(player.getUniqueID())) {
                    continue;
                }
                final NetHandlerPlayServer handler = player.playerNetServerHandler;
                if (handler == null || !managers.contains(handler.func_147362_b())) {
                    stranded.add(player);
                }
            }
        }

        return !live.isEmpty() || !stranded.isEmpty() || !accepting.isEmpty();
    }

    /** Runs the cleanup networkTick can no longer reach, so the next login attempt finds a clear world. */
    @Unique
    private void hodgepodge$repairStrandedSession(EntityPlayerMP player) {
        final NetHandlerPlayServer handler = player.playerNetServerHandler;
        if (handler == null) {
            // writePlayerData would refuse to save them, so removing them would silently drop their progress.
            Common.log.error(
                    "{} is in the world with no connection at all and cannot be cleaned up safely; they will not be "
                            + "able to log in until the server restarts",
                    player.getCommandSenderName());
            return;
        }

        Common.log.warn(
                "{} is still in the world after their connection went away, so their disconnect never ran. Saving and "
                        + "removing them now so their next login attempt can succeed.",
                player.getCommandSenderName());
        try {
            handler.onDisconnect(new ChatComponentText(hodgepodge$KICK_REASON));
        } catch (Throwable t) {
            Common.log.error("Failed to clean up the stale session for {}", player.getCommandSenderName(), t);
        }
    }
}
