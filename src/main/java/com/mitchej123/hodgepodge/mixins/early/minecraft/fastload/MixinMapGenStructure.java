package com.mitchej123.hodgepodge.mixins.early.minecraft.fastload;

import java.util.Map;

import net.minecraft.world.gen.structure.MapGenStructure;
import net.minecraft.world.gen.structure.StructureStart;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalLongRef;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;

@Mixin(MapGenStructure.class)
public class MixinMapGenStructure {

    @Shadow
    protected Map<Long, StructureStart> structureMap = new Long2ObjectOpenHashMap<>();

    @Redirect(
            method = "func_151538_a",
            at = @At(value = "INVOKE", target = "Ljava/lang/Long;valueOf(J)Ljava/lang/Long;"))
    private Long hodgepodge$nukeBox(long l, @Share("unboxedLong") LocalLongRef unboxedLong) {
        unboxedLong.set(l);
        return null;
    }

    @Redirect(
            method = "func_151538_a",
            at = @At(value = "INVOKE", target = "Ljava/util/Map;containsKey(Ljava/lang/Object;)Z"))
    private boolean hodgepodge$primitiveContains(Map<Long, StructureStart> instance, Object o,
            @Share("unboxedLong") LocalLongRef unboxedLong) {
        return ((Long2ObjectMap<StructureStart>) structureMap).containsKey(unboxedLong.get());
    }

    @Redirect(
            method = "func_151538_a",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/Map;put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;",
                    ordinal = 0))
    private Object hodgepodge$primitiveContains(Map<Long, StructureStart> instance, Object k, Object v,
            @Share("unboxedLong") LocalLongRef unboxedLong) {
        return ((Long2ObjectMap<StructureStart>) structureMap).put(unboxedLong.get(), (StructureStart) v);
    }
}
