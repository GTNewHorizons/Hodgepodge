package com.mitchej123.hodgepodge.mixins.interfaces;

import java.util.UUID;

/**
 * Login bookkeeping on {@link net.minecraft.server.network.NetHandlerLoginServer}, so a second login for the same UUID
 * can see one that has already been accepted but has not reached the world yet.
 */
public interface AcceptedLogin {

    /** The UUID this login was accepted for, or null while it is still being decided. */
    UUID hodgepodge$getAcceptedUuid();
}
