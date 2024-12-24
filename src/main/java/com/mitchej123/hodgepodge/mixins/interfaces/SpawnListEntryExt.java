package com.mitchej123.hodgepodge.mixins.interfaces;

import java.lang.reflect.InvocationTargetException;

import net.minecraft.entity.EntityLiving;
import net.minecraft.world.World;

public interface SpawnListEntryExt {

    EntityLiving constructEntity(World world)
            throws InvocationTargetException, IllegalAccessException, NoSuchMethodException;
}
