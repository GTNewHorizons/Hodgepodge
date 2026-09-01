package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.network.NetHandlerPlayServer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import com.mitchej123.hodgepodge.mixins.interfaces.LoginSessionState;

@Mixin(NetHandlerPlayServer.class)
public class MixinNetHandlerPlayServer_LoginSessionState implements LoginSessionState {

    @Unique
    private volatile boolean hodgepodge$loggingIn;

    @Unique
    private volatile boolean hodgepodge$superseded;

    @Override
    public boolean hodgepodge$isLoggingIn() {
        return this.hodgepodge$loggingIn;
    }

    @Override
    public void hodgepodge$setLoggingIn(boolean loggingIn) {
        this.hodgepodge$loggingIn = loggingIn;
    }

    @Override
    public boolean hodgepodge$isSuperseded() {
        return this.hodgepodge$superseded;
    }

    @Override
    public void hodgepodge$setSuperseded(boolean superseded) {
        this.hodgepodge$superseded = superseded;
    }
}
