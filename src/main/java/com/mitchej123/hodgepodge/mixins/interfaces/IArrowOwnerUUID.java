package com.mitchej123.hodgepodge.mixins.interfaces;

import java.util.UUID;

public interface IArrowOwnerUUID {

    UUID hodgepodge$getCachedOwnerUUID();

    void hodgepodge$setCachedOwnerUUID(UUID uuid);
}
