package com.mitchej123.hodgepodge.mixins.late.biomesoplenty;

import java.util.Random;

import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import biomesoplenty.common.world.features.WorldGenKelp;

@Mixin(value = WorldGenKelp.class)
public class MixinWorldGenKelp {

    /**
     * @author Charsy89
     * @reason Biomes O' Plenty causes cascading worldgen when generating kelp. This occurs because no +8 offset is
     *         added to the random coordinates provided to the generator by the game; this mixin modifies the X/Z
     *         coordinates passed to generate() so that they are offset by +8 blocks.
     */
    @Redirect(
            method = "setupGeneration(Lnet/minecraft/world/World;Ljava/util/Random;Lbiomesoplenty/api/biome/BOPBiome;Ljava/lang/String;II)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lbiomesoplenty/common/world/features/WorldGenKelp;generate(Lnet/minecraft/world/World;Ljava/util/Random;III)Z",
                    remap = true),
            remap = false)
    private boolean hodgepodge$offsetKelpBy8(WorldGenKelp featureGen, World world, Random random, int x, int y, int z) {
        return featureGen.generate(world, random, x + 8, y, z + 8);
    }
}
