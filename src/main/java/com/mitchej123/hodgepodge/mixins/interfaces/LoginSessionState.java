package com.mitchej123.hodgepodge.mixins.interfaces;

/**
 * Login bookkeeping on {@link net.minecraft.network.NetHandlerPlayServer}, so a second login for the same UUID can tell
 * what the session it is displacing is doing. Written from the Netty IO thread, read from the server thread.
 */
public interface LoginSessionState {

    /** True while initializeConnectionToPlayer is running for this session. */
    boolean hodgepodge$isLoggingIn();

    void hodgepodge$setLoggingIn(boolean loggingIn);

    /** True once a newer login for the same UUID has kicked this session. */
    boolean hodgepodge$isSuperseded();

    void hodgepodge$setSuperseded(boolean superseded);
}
