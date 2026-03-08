package com.mitchej123.hodgepodge.mixins.hooks;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.world.World;

public class BukkitSpawnHelper {
    private static final Object SPAWN_REASON_NATURAL;
    private static final Object SPAWN_REASON_CHUNK_GEN;
    private static final MethodHandle ADD_ENTITY;
    private static final MethodHandle GET_BUKKIT_WORLD;
    private static final MethodHandle GET_MONSTER_SPAWN_LIMIT;
    private static final MethodHandle GET_ANIMAL_SPAWN_LIMIT;
    private static final MethodHandle GET_WATER_ANIMAL_SPAWN_LIMIT;
    private static final MethodHandle GET_AMBIENT_SPAWN_LIMIT;
    private static final MethodHandle GET_SPIGOT_CONFIG;
    private static final java.lang.reflect.Field MOB_SPAWN_RANGE_FIELD;

    static {
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        Object natural = null;
        Object chunkGen = null;
        MethodHandle addEntity = null;
        MethodHandle getBukkitWorld = null;
        MethodHandle getMonster = null;
        MethodHandle getAnimal = null;
        MethodHandle getWaterAnimal = null;
        MethodHandle getAmbient = null;
        MethodHandle getSpigotConfig = null;
        java.lang.reflect.Field mobSpawnRangeField = null;

        try {
            Class<?> spawnReasonClass = Class.forName("org.bukkit.event.entity.CreatureSpawnEvent$SpawnReason");
            natural = Enum.valueOf(spawnReasonClass.asSubclass(Enum.class), "NATURAL");
            chunkGen = Enum.valueOf(spawnReasonClass.asSubclass(Enum.class), "CHUNK_GEN");

            addEntity = lookup.findVirtual(
                    World.class,
                    "addEntity",
                    MethodType.methodType(boolean.class, Entity.class, spawnReasonClass));

            Class<?> craftWorldClass = Class.forName("org.bukkit.craftbukkit.CraftWorld");
            getBukkitWorld = lookup.findVirtual(World.class, "getWorld", MethodType.methodType(craftWorldClass));
            getMonster = lookup.findVirtual(craftWorldClass, "getMonsterSpawnLimit", MethodType.methodType(int.class));
            getAnimal = lookup.findVirtual(craftWorldClass, "getAnimalSpawnLimit", MethodType.methodType(int.class));
            getWaterAnimal = lookup
                    .findVirtual(craftWorldClass, "getWaterAnimalSpawnLimit", MethodType.methodType(int.class));
            getAmbient = lookup.findVirtual(craftWorldClass, "getAmbientSpawnLimit", MethodType.methodType(int.class));

            Class<?> spigotConfigClass = Class.forName("org.spigotmc.SpigotWorldConfig");
            getSpigotConfig = lookup
                    .findVirtual(World.class, "getSpigotConfig", MethodType.methodType(spigotConfigClass));
            mobSpawnRangeField = spigotConfigClass.getField("mobSpawnRange");
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException
                | NoSuchFieldException ignored) {}

        SPAWN_REASON_NATURAL = natural;
        SPAWN_REASON_CHUNK_GEN = chunkGen;
        ADD_ENTITY = addEntity;
        GET_BUKKIT_WORLD = getBukkitWorld;
        GET_MONSTER_SPAWN_LIMIT = getMonster;
        GET_ANIMAL_SPAWN_LIMIT = getAnimal;
        GET_WATER_ANIMAL_SPAWN_LIMIT = getWaterAnimal;
        GET_AMBIENT_SPAWN_LIMIT = getAmbient;
        GET_SPIGOT_CONFIG = getSpigotConfig;
        MOB_SPAWN_RANGE_FIELD = mobSpawnRangeField;
    }

    public static boolean spawnEntity(World world, Entity entity, boolean isWorldGen) {
        if (ADD_ENTITY != null) {
            Object reason = isWorldGen ? SPAWN_REASON_CHUNK_GEN : SPAWN_REASON_NATURAL;
            if (reason != null) {
                try {
                    return (boolean) ADD_ENTITY.invoke(world, entity, reason);
                } catch (Throwable ignored) {}
            }
        }
        return world.spawnEntityInWorld(entity);
    }

    public static int getSpawnLimit(World world, EnumCreatureType type) {
        if (GET_BUKKIT_WORLD != null) {
            try {
                Object craftWorld = GET_BUKKIT_WORLD.invoke(world);
                MethodHandle getter = switch (type) {
                    case monster -> GET_MONSTER_SPAWN_LIMIT;
                    case creature -> GET_ANIMAL_SPAWN_LIMIT;
                    case waterCreature -> GET_WATER_ANIMAL_SPAWN_LIMIT;
                    case ambient -> GET_AMBIENT_SPAWN_LIMIT;
                };
                if (getter != null) {
                    int limit = (int) getter.invoke(craftWorld);
                    if (limit >= 0) return limit;
                }
            } catch (Throwable ignored) {}
        }
        return type.getMaxNumberOfCreature();
    }

    public static int getMobSpawnRange(World world) {
        if (GET_SPIGOT_CONFIG != null && MOB_SPAWN_RANGE_FIELD != null) {
            try {
                Object config = GET_SPIGOT_CONFIG.invoke(world);
                return MOB_SPAWN_RANGE_FIELD.getInt(config);
            } catch (Throwable ignored) {}
        }
        return -1;
    }
}
