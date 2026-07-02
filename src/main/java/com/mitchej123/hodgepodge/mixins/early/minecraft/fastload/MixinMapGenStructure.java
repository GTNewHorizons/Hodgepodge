package com.mitchej123.hodgepodge.mixins.early.minecraft.fastload;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import net.minecraft.world.gen.structure.MapGenStructure;
import net.minecraft.world.gen.structure.StructureStart;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;

@Mixin(MapGenStructure.class)
public class MixinMapGenStructure {

    @Shadow
    protected Map<Long, StructureStart> structureMap = new Long2ObjectOpenHashMap<>();

    private long hodgepodge$localRef;

    @Redirect(
            method = "func_151538_a",
            at = @At(value = "INVOKE", target = "Ljava/lang/Long;valueOf(J)Ljava/lang/Long;"))
    private Long hodgepodge$nukeBox(long l) {
        hodgepodge$localRef = l;
        return null;
    }

    @WrapOperation(
            method = "func_151538_a",
            at = @At(value = "INVOKE", target = "Ljava/util/Map;containsKey(Ljava/lang/Object;)Z"))
    private boolean hodgepodge$primitiveContains(Map<Long, StructureStart> instance, Object o,
            Operation<Boolean> original) {
        return ((Long2ObjectOpenHashMap<StructureStart>) structureMap).containsKey(hodgepodge$localRef);
    }

    @Redirect(
            method = "func_151538_a",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/Map;put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;",
                    ordinal = 0))
    private Object hodgepodge$primitiveContains(Map<Long, StructureStart> instance, Object k, Object v) {
        return ((Long2ObjectOpenHashMap<StructureStart>) structureMap).put(hodgepodge$localRef, (StructureStart) v);
    }

    @Redirect(
            method = { "generateStructuresInChunk(Lnet/minecraft/world/World;Ljava/util/Random;II)Z",
                    "func_143028_c(III)Lnet/minecraft/world/gen/structure/StructureStart;", "func_142038_b(III)Z",
                    "func_151545_a(Lnet/minecraft/world/World;III)Lnet/minecraft/world/ChunkPosition;" },
            at = @At(value = "INVOKE", target = "Ljava/util/Map;values()Ljava/util/Collection;"))
    private Collection<StructureStart> hodgepodge$snapshotStructureStarts(Map<Long, StructureStart> instance) {
        return new ArrayList<>(instance.values());
    }
}
