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
 * A throwing FML disconnect listener normally prevents the rest of NetworkDispatcher.close from closing the channel.
 * EventBus has already logged the exception, so a superseded session must be allowed to finish closing.
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
            expect = 2)
    private boolean hodgepodge$finishSupersededClose(EventBus bus, Event event, Operation<Boolean> original) {
        try {
            return original.call(bus, event);
        } catch (RuntimeException | Error exception) {
            if (!LoginSessionState.isSuperseded(this.manager)) {
                throw exception;
            }
            return false;
        }
    }
}
