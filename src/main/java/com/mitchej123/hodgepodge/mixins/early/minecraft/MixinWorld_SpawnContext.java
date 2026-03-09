package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import com.mitchej123.hodgepodge.mixins.interfaces.SpawnContextWorld;

@Mixin(World.class)
public class MixinWorld_SpawnContext implements SpawnContextWorld {

    @Unique
    private int hodgepodge$spawnContext;

    @Override
    public int hodgepodge$getSpawnContext() {
        return hodgepodge$spawnContext;
    }

    @Override
    public void hodgepodge$setSpawnContext(int context) {
        hodgepodge$spawnContext = context;
    }
}
