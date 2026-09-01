package com.mitchej123.hodgepodge.mixins.early.fml;

import net.minecraft.network.NetHandlerPlayServer;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mitchej123.hodgepodge.mixins.interfaces.LoginSessionState;

import cpw.mods.fml.common.network.handshake.NetworkDispatcher;
import cpw.mods.fml.relauncher.Side;

/**
 * Marks the window during which a session is being put into the world, so that a login for the same UUID arriving on
 * the server thread does not disconnect it partway through.
 * <p>
 * Wrapped on completeHandshake, which calls initializeConnectionToPlayer, rather than on that method itself: Forge
 * patched an extra parameter onto it, so its three-argument form has no obfuscation mapping to target. A throw has to
 * clear the flag as well, or the UUID stays blocked until the login timeout.
 */
@Mixin(NetworkDispatcher.class)
public abstract class MixinNetworkDispatcher_LoginSessionState {

    @Shadow(remap = false)
    @Final
    private Side side;

    @Shadow(remap = false)
    private NetHandlerPlayServer serverHandler;

    @WrapMethod(method = "completeHandshake", remap = false)
    private void hodgepodge$bracketLogin(Side target, Operation<Void> original) {
        if (this.side != Side.SERVER || this.serverHandler == null) {
            original.call(target);
            return;
        }
        final LoginSessionState state = (LoginSessionState) this.serverHandler;
        state.hodgepodge$setLoggingIn(true);
        try {
            original.call(target);
        } finally {
            state.hodgepodge$setLoggingIn(false);
        }
    }
}
