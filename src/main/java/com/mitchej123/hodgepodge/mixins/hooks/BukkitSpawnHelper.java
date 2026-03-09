package com.mitchej123.hodgepodge.mixins.hooks;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

import net.minecraft.entity.EnumCreatureType;
import net.minecraft.world.World;

public class BukkitSpawnHelper {

    private static final MethodHandle GET_BUKKIT_WORLD;
    private static final MethodHandle GET_MONSTER_SPAWN_LIMIT;
    private static final MethodHandle GET_ANIMAL_SPAWN_LIMIT;
    private static final MethodHandle GET_WATER_ANIMAL_SPAWN_LIMIT;
    private static final MethodHandle GET_AMBIENT_SPAWN_LIMIT;
    private static final MethodHandle GET_SPIGOT_CONFIG;
    private static final java.lang.reflect.Field MOB_SPAWN_RANGE_FIELD;

    static {
        final MethodHandles.Lookup lookup = MethodHandles.lookup();
        MethodHandle getBukkitWorld = null;
        MethodHandle getMonster = null;
        MethodHandle getAnimal = null;
        MethodHandle getWaterAnimal = null;
        MethodHandle getAmbient = null;
        MethodHandle getSpigotConfig = null;
        java.lang.reflect.Field mobSpawnRangeField = null;

        try {
            final Class<?> craftWorldClass = Class.forName("org.bukkit.craftbukkit.CraftWorld");
            getBukkitWorld = lookup.findVirtual(World.class, "getWorld", MethodType.methodType(craftWorldClass));
            getMonster = lookup.findVirtual(craftWorldClass, "getMonsterSpawnLimit", MethodType.methodType(int.class));
            getAnimal = lookup.findVirtual(craftWorldClass, "getAnimalSpawnLimit", MethodType.methodType(int.class));
            getWaterAnimal = lookup
                    .findVirtual(craftWorldClass, "getWaterAnimalSpawnLimit", MethodType.methodType(int.class));
            getAmbient = lookup.findVirtual(craftWorldClass, "getAmbientSpawnLimit", MethodType.methodType(int.class));

            final Class<?> spigotConfigClass = Class.forName("org.spigotmc.SpigotWorldConfig");
            getSpigotConfig = lookup
                    .findVirtual(World.class, "getSpigotConfig", MethodType.methodType(spigotConfigClass));
            mobSpawnRangeField = spigotConfigClass.getField("mobSpawnRange");
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException
                | NoSuchFieldException ignored) {}

        GET_BUKKIT_WORLD = getBukkitWorld;
        GET_MONSTER_SPAWN_LIMIT = getMonster;
        GET_ANIMAL_SPAWN_LIMIT = getAnimal;
        GET_WATER_ANIMAL_SPAWN_LIMIT = getWaterAnimal;
        GET_AMBIENT_SPAWN_LIMIT = getAmbient;
        GET_SPIGOT_CONFIG = getSpigotConfig;
        MOB_SPAWN_RANGE_FIELD = mobSpawnRangeField;
    }

    public static int getSpawnLimit(World world, EnumCreatureType type) {
        if (GET_BUKKIT_WORLD != null) {
            try {
                final Object craftWorld = GET_BUKKIT_WORLD.invoke(world);
                final MethodHandle getter = switch (type) {
                    case monster -> GET_MONSTER_SPAWN_LIMIT;
                    case creature -> GET_ANIMAL_SPAWN_LIMIT;
                    case waterCreature -> GET_WATER_ANIMAL_SPAWN_LIMIT;
                    case ambient -> GET_AMBIENT_SPAWN_LIMIT;
                };
                if (getter != null) {
                    final int limit = (int) getter.invoke(craftWorld);
                    if (limit >= 0) return limit;
                }
            } catch (Throwable ignored) {}
        }
        return type.getMaxNumberOfCreature();
    }

    public static int getMobSpawnRange(World world) {
        if (GET_SPIGOT_CONFIG != null && MOB_SPAWN_RANGE_FIELD != null) {
            try {
                final Object config = GET_SPIGOT_CONFIG.invoke(world);
                return MOB_SPAWN_RANGE_FIELD.getInt(config);
            } catch (Throwable ignored) {}
        }
        return -1;
    }
}
