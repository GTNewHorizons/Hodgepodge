package com.mitchej123.hodgepodge.mixins.late.biomesoplenty;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import biomesoplenty.common.biome.overworld.BiomeGenAlps;
import biomesoplenty.common.biome.overworld.BiomeGenCrag;
import biomesoplenty.common.biome.overworld.BiomeGenHighland;
import biomesoplenty.common.biome.overworld.BiomeGenJadeCliffs;
import biomesoplenty.common.biome.overworld.BiomeGenMountain;
import biomesoplenty.common.biome.overworld.sub.BiomeGenAlpsForest;
import biomesoplenty.common.configuration.BOPConfigurationTerrainGen;

@Mixin({ BiomeGenAlps.class, BiomeGenAlpsForest.class, BiomeGenCrag.class, BiomeGenHighland.class,
        BiomeGenJadeCliffs.class, BiomeGenMountain.class })
public class MixinBiomeEmeraldGeneration {

    @ModifyExpressionValue(
            method = "decorate",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/Block;isReplaceableOreGen(Lnet/minecraft/world/World;IIILnet/minecraft/block/Block;)Z",
                    remap = false))
    private boolean hodgepodge$checkOreGeneration(boolean original) {
        return original && BOPConfigurationTerrainGen.genOreGeneral;
    }
}
