package com.mitchej123.hodgepodge.mixins.early.minecraft;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.InvocationTargetException;

import net.minecraft.entity.EntityLiving;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import com.mitchej123.hodgepodge.mixins.interfaces.SpawnListEntryExt;

@Mixin(BiomeGenBase.SpawnListEntry.class)
public class MixinSpawnListEntry_optimizeSpawning implements SpawnListEntryExt {

    @Shadow
    public Class<? extends net.minecraft.entity.EntityLiving> entityClass;

    private MethodHandle hodgepodge$cachedConstructorHandle;

    @Override
    public EntityLiving constructEntity(World world)
            throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        if (hodgepodge$cachedConstructorHandle == null) {
            MethodType constructorType = MethodType.methodType(void.class, World.class);
            hodgepodge$cachedConstructorHandle = MethodHandles.lookup().findConstructor(entityClass, constructorType);
        }
        try {
            return (EntityLiving) hodgepodge$cachedConstructorHandle.invoke(world);
        } catch (Throwable throwable) {
            throw new InvocationTargetException(throwable);
        }
    }
}
