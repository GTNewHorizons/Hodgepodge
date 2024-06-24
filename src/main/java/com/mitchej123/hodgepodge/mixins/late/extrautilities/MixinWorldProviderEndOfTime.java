package com.mitchej123.hodgepodge.mixins.late.extrautilities;

import net.minecraft.world.WorldProvider;
import net.minecraft.world.chunk.Chunk;

import org.spongepowered.asm.mixin.Mixin;

import com.rwtema.extrautils.worldgen.endoftime.WorldProviderEndOfTime;

@Mixin(WorldProviderEndOfTime.class)
@SuppressWarnings("unused")
public abstract class MixinWorldProviderEndOfTime extends WorldProvider {

    @Override
    public boolean canDoRainSnowIce(Chunk chunk) {
        return false;
    }
}
