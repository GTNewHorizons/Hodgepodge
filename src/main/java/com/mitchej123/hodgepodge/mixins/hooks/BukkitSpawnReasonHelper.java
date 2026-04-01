package com.mitchej123.hodgepodge.mixins.hooks;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;

import com.mitchej123.hodgepodge.mixins.interfaces.SpawnContextWorld;

public class BukkitSpawnReasonHelper {

    private static final Object SPAWN_REASON_NATURAL;
    private static final Object SPAWN_REASON_CHUNK_GEN;
    private static final MethodHandle ADD_ENTITY;

    static {
        final MethodHandles.Lookup lookup = MethodHandles.lookup();
        Object natural = null;
        Object chunkGen = null;
        MethodHandle addEntity = null;

        try {
            final Class<?> spawnReasonClass = Class.forName("org.bukkit.event.entity.CreatureSpawnEvent$SpawnReason");
            natural = Enum.valueOf(spawnReasonClass.asSubclass(Enum.class), "NATURAL");
            chunkGen = Enum.valueOf(spawnReasonClass.asSubclass(Enum.class), "CHUNK_GEN");

            addEntity = lookup.findVirtual(
                    World.class,
                    "addEntity",
                    MethodType.methodType(boolean.class, Entity.class, spawnReasonClass));
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException ignored) {}

        SPAWN_REASON_NATURAL = natural;
        SPAWN_REASON_CHUNK_GEN = chunkGen;
        ADD_ENTITY = addEntity;
    }

    /** @return the result of the Bukkit addEntity call, or null if not applicable */
    public static Boolean routeSpawnReason(World world, Entity entity) {
        final int context = ((SpawnContextWorld) world).hodgepodge$getSpawnContext();
        if (context == SpawnContextWorld.CONTEXT_NONE || ADD_ENTITY == null) return null;

        final Object reason = context == SpawnContextWorld.CONTEXT_CHUNK_GEN ? SPAWN_REASON_CHUNK_GEN
                : SPAWN_REASON_NATURAL;
        if (reason == null) return null;

        try {
            return (boolean) ADD_ENTITY.invoke(world, entity, reason);
        } catch (Throwable ignored) {
            return null;
        }
    }
}
