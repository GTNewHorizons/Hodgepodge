package com.mitchej123.hodgepodge.mixins.early.minecraft;

import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.NetworkManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraft.server.network.NetHandlerLoginServer;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mitchej123.hodgepodge.util.LoginSessionIndex;
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

        final LoginSessionIndex index = ((LoginSessionIndex.Provider) server.func_147137_ag())
                .hodgepodge$getLoginSessionIndex();
        LoginSessionIndex.Sessions sessions = index.getSessions(uuid);
        if (sessions.isEmpty()) {
            this.hodgepodge$ticksWaited = 0;
            index.accept(this.field_147333_a, uuid);
            return;
        }
        final int waited = sessions.waited(this.hodgepodge$ticksWaited);
        // Every waiter gives the most recently superseded session the same full cleanup interval.
        this.hodgepodge$ticksWaited = waited + 1;
        if (waited >= hodgepodge$GIVE_UP_AFTER) {
            sessions.repairStranded();
            sessions = index.getSessions(uuid);
            if (sessions.isEmpty()) {
                // The repair freed the last blocker, so there is nothing left to wait for.
                this.hodgepodge$ticksWaited = 0;
                index.accept(this.field_147333_a, uuid);
                return;
            }
            // func_147322_a leaves the login state alone, so onNetworkTick keeps calling us until the channel is
            // actually seen closed a tick or two later.
            this.hodgepodge$gaveUp = true;
            this.func_147322_a(
                    !sessions.hasStranded()
                            ? "Your previous session is still being cleaned up, please reconnect in a moment"
                            : "Your previous session could not be cleaned up safely. Please contact a server administrator.");
        }

        ci.cancel();
    }

}
