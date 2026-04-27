package com.mitchej123.hodgepodge.mixins.late.biomesoplenty;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import biomesoplenty.common.world.features.WorldGenKelp;

@Mixin(value = WorldGenKelp.class)
public class MixinWorldGenKelp {

    /**
     * @author Charsy89
     * @reason Biomes O' Plenty causes cascading worldgen when generating kelp. This occurs because no +8 offset is
     *         added to the random coordinates provided to the generator by the game; this mixin modifies the X/Z
     *         coordinates passed to generate() inside of setupGeneration() so that they are offset by +8 blocks.
     */
    @ModifyArgs(
            method = "setupGeneration(Lnet/minecraft/world/World;Ljava/util/Random;Lbiomesoplenty/api/biome/BOPBiome;Ljava/lang/String;II)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lbiomesoplenty/common/world/features/WorldGenKelp;generate(Lnet/minecraft/world/World;Ljava/util/Random;III)Z",
                    remap = true),
            remap = false)
    private void hodgepodge$offsetKelpBy8(Args args) {
        args.<Integer>set(2, args.<Integer>get(2) + 8); // randX
        args.<Integer>set(4, args.<Integer>get(2) + 8); // randZ
    }
}
