package com.mitchej123.hodgepodge.mixins.late.biomesoplenty;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import biomesoplenty.api.content.BOPCBlocks;
import biomesoplenty.common.biome.overworld.BiomeGenLavenderFields;
import biomesoplenty.common.world.features.WorldGenBOPFlora;

@Mixin(value = BiomeGenLavenderFields.class, remap = false)
public class MixinBiomeGenLavenderFields {

    @Inject(method = "<init>", at = @At("TAIL"))
    private void addLavenderToFlowerPool(int id, CallbackInfo ci) {
        ((BiomeGenLavenderFields) (Object) this).theBiomeDecorator.bopFeatures.weightedFlowerGen
                .put(new WorldGenBOPFlora(BOPCBlocks.flowers2, 3), 12);
    }
}
