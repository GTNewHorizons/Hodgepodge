package com.mitchej123.hodgepodge.mixins.early.fml;

import net.minecraft.network.NetworkManager;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mitchej123.hodgepodge.mixins.interfaces.LoginSessionState;

import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.EventBus;
import cpw.mods.fml.common.network.handshake.NetworkDispatcher;

/**
 * Keeps a superseded session closing cleanly. Its disconnect event is posted once no matter how many closes reach the
 * pipeline, including one that ran before the barrier superseded the session, and a throwing listener cannot hold the
 * close back, since EventBus has already logged the exception.
 */
@Mixin(NetworkDispatcher.class)
public abstract class MixinNetworkDispatcher_LoginSessionState {

    @Shadow(remap = false)
    @Final
    public NetworkManager manager;

    @WrapOperation(
            method = "close",
            at = @At(
                    value = "INVOKE",
                    target = "Lcpw/mods/fml/common/eventhandler/EventBus;post(Lcpw/mods/fml/common/eventhandler/Event;)Z"),
            remap = false,
            expect = 2,
            require = 2)
    private boolean hodgepodge$finishSupersededClose(EventBus bus, Event event, Operation<Boolean> original) {
        // Recorded on every close, so one that ran before the barrier stepped in still counts as the post.
        final boolean firstPost = LoginSessionState.markDisconnectPosted(this.manager);
        if (!LoginSessionState.isSuperseded(this.manager)) {
            return original.call(bus, event);
        }
        if (!firstPost) {
            return false;
        }
        try {
            return original.call(bus, event);
        } catch (RuntimeException | Error exception) {
            return false;
        }
    }
}
