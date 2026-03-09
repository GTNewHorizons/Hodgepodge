package com.mitchej123.hodgepodge.mixins.early.minecraft;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.mitchej123.hodgepodge.mixins.interfaces.SpawnContextWorld;

@Mixin(World.class)
public class MixinWorld_BukkitSpawnReason {

    @Unique
    private static final Object hodgepodge$SPAWN_REASON_NATURAL;

    @Unique
    private static final Object hodgepodge$SPAWN_REASON_CHUNK_GEN;

    @Unique
    private static final MethodHandle hodgepodge$ADD_ENTITY;

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

        hodgepodge$SPAWN_REASON_NATURAL = natural;
        hodgepodge$SPAWN_REASON_CHUNK_GEN = chunkGen;
        hodgepodge$ADD_ENTITY = addEntity;
    }

    @Inject(method = "spawnEntityInWorld", at = @At("HEAD"), cancellable = true)
    private void hodgepodge$routeSpawnReason(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        final int context = ((SpawnContextWorld) this).hodgepodge$getSpawnContext();
        if (context == SpawnContextWorld.CONTEXT_NONE || hodgepodge$ADD_ENTITY == null) return;

        final Object reason = context == SpawnContextWorld.CONTEXT_CHUNK_GEN ? hodgepodge$SPAWN_REASON_CHUNK_GEN
                : hodgepodge$SPAWN_REASON_NATURAL;
        if (reason == null) return;

        try {
            cir.setReturnValue((boolean) hodgepodge$ADD_ENTITY.invoke(this, entity, reason));
        } catch (Throwable ignored) {}
    }
}
