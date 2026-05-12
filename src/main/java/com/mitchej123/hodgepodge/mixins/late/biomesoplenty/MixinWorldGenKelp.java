package com.mitchej123.hodgepodge.mixins.late.biomesoplenty;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import biomesoplenty.common.world.features.WorldGenKelp;

@Mixin(value = WorldGenKelp.class)
public class MixinWorldGenKelp {

    /**
     * @author Charsy89
     * @reason Biomes O' Plenty causes cascading worldgen when generating kelp. This occurs because no +8 offset is
     *         added to the random coordinates provided to the generator by the game; this mixin modifies the X
     *         coordinate passed to generate() so that it is offset by +8 blocks.
     */
    @ModifyVariable(
            method = "Lbiomesoplenty/common/world/features/WorldGenKelp;generate(Lnet/minecraft/world/World;Ljava/util/Random;III)Z",
            argsOnly = true,
            at = @At(value = "HEAD"),
            remap = true,
            ordinal = 0)
    private int hodgepodge$offsetKelpXBy8(int x) {
        return x + 8;
    }

    /**
     * @author Charsy89
     * @reason Biomes O' Plenty causes cascading worldgen when generating kelp. This occurs because no +8 offset is
     *         added to the random coordinates provided to the generator by the game; this mixin modifies the X
     *         coordinate passed to generate() so that it is offset by +8 blocks.
     */
    @ModifyVariable(
            method = "Lbiomesoplenty/common/world/features/WorldGenKelp;generate(Lnet/minecraft/world/World;Ljava/util/Random;III)Z",
            argsOnly = true,
            at = @At(value = "HEAD"),
            remap = true,
            ordinal = 2)
    private int hodgepodge$offsetKelpZBy8(int z) {
        return z + 8;
    }
}
