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

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mitchej123.hodgepodge.Common;
import com.mitchej123.hodgepodge.mixins.interfaces.AcceptedLogin;
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
 * is a free retry: kick once, then wait for networkTick to do the cleanup on its usual code path. Both the kick and the
 * cleanup run on the server thread, so their ordering is guaranteed rather than raced for.
 */
@Mixin(NetHandlerLoginServer.class)
public abstract class MixinNetHandlerLoginServer_AwaitPreviousSession implements AcceptedLogin {

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
    private UUID hodgepodge$loginUuid;

    @Unique
    private boolean hodgepodge$accepted;

    @Unique
    private boolean hodgepodge$gaveUp;

    @Shadow
    private GameProfile field_147337_i;

    @Shadow
    @Final
    public NetworkManager field_147333_a;

    @Shadow
    public abstract void func_147322_a(String reason);

    @Shadow
    protected abstract GameProfile func_152506_a(GameProfile original);

    @Override
    public UUID hodgepodge$getAcceptedUuid() {
        return this.hodgepodge$accepted ? this.hodgepodge$loginUuid : null;
    }

    @Inject(
            method = "func_147326_c",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/management/ServerConfigurationManager;createPlayerForUser(Lcom/mojang/authlib/GameProfile;)Lnet/minecraft/entity/player/EntityPlayerMP;"))
    private void hodgepodge$markAccepted(CallbackInfo ci) {
        this.hodgepodge$accepted = true;
    }

    @Inject(method = "func_147326_c", at = @At("HEAD"), cancellable = true)
    private void hodgepodge$awaitPreviousSession(CallbackInfo ci) {
        final MinecraftServer server = MinecraftServer.getServer();
        if (server == null || this.field_147337_i == null) {
            return;
        }
        final ServerConfigurationManager scm = server.getConfigurationManager();
        if (scm == null) {
            return;
        }

        if (this.hodgepodge$loginUuid == null) {
            // func_147326_c completes the profile itself, but only after we run.
            GameProfile profile = this.field_147337_i;
            if (!profile.isComplete()) {
                profile = this.func_152506_a(profile);
            }
            this.hodgepodge$loginUuid = EntityPlayer.func_146094_a(profile);
        }
        final UUID uuid = this.hodgepodge$loginUuid;

        final List<NetworkManager> live = new ArrayList<>();
        final List<EntityPlayerMP> stranded = new ArrayList<>();
        final boolean loggingIn = this.hodgepodge$collectSessions(server, scm, uuid, live, stranded);

        if (loggingIn) {
            // Disconnecting a session mid initializeConnectionToPlayer would run its PlayerLoggedOutEvent handlers
            // before its PlayerLoggedInEvent ones. It is making progress, so this tick is not held against it.
            this.hodgepodge$ticksWaited = 0;
            ci.cancel();
            return;
        }

        if (live.isEmpty() && stranded.isEmpty()) {
            this.hodgepodge$ticksWaited = 0;
            return;
        }

        final int waited = this.hodgepodge$ticksWaited++;

        for (NetworkManager manager : live) {
            final NetHandlerPlayServer handler = (NetHandlerPlayServer) manager.getNetHandler();
            final LoginSessionState state = (LoginSessionState) handler;
            if (!state.hodgepodge$isSuperseded()) {
                state.hodgepodge$setSuperseded(true);
                handler.kickPlayerFromServer(hodgepodge$KICK_REASON);
                // The kick stops further reads but leaves the queue to be dispatched; this session is about to be
                // saved and removed, so applying more of its input is worse than dropping it.
                ((NetworkManagerInboundAccessor) manager).hodgepodge$getReceivedPacketsQueue().clear();
            } else if (waited >= hodgepodge$FORCE_CLOSE_AFTER && manager.isChannelOpen()) {
                // kickPlayerFromServer closes only once the disconnect packet has been written, which on a half-dead
                // connection means waiting out the OS retransmit timeout - exactly the case this fix exists for.
                manager.closeChannel(new ChatComponentText(hodgepodge$KICK_REASON));
            }
        }

        if (waited >= hodgepodge$GIVE_UP_AFTER && !this.hodgepodge$gaveUp) {
            // func_147322_a leaves the login state alone, so onNetworkTick keeps calling us until the channel is
            // actually seen closed a tick or two later.
            this.hodgepodge$gaveUp = true;
            for (EntityPlayerMP player : stranded) {
                this.hodgepodge$repairStrandedSession(player);
            }
            this.func_147322_a("Your previous session is still being cleaned up, please reconnect in a moment");
        }

        ci.cancel();
    }

    /** Returns true if one of the live sessions is currently being put into the world. */
    @Unique
    private boolean hodgepodge$collectSessions(MinecraftServer server, ServerConfigurationManager scm, UUID uuid,
            List<NetworkManager> live, List<EntityPlayerMP> stranded) {
        boolean loggingIn = false;
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
                if (handler instanceof NetHandlerLoginServer) {
                    // FML starts the handshake from a Netty task, so a login accepted earlier in this same networkTick
                    // has not installed its NetHandlerPlayServer yet and would otherwise be invisible here.
                    loggingIn |= uuid.equals(((AcceptedLogin) handler).hodgepodge$getAcceptedUuid());
                    continue;
                }
                // NetHandlerPlayServer is installed at the start of the handshake, so this sees pending logins too.
                if (!(handler instanceof NetHandlerPlayServer)) {
                    continue;
                }
                final EntityPlayerMP player = ((NetHandlerPlayServer) handler).playerEntity;
                if (player == null || !uuid.equals(player.getUniqueID())) {
                    continue;
                }
                live.add(manager);
                loggingIn |= ((LoginSessionState) handler).hodgepodge$isLoggingIn();
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

        return loggingIn;
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
