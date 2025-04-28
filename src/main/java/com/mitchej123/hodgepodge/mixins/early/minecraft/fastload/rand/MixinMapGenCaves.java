package com.mitchej123.hodgepodge.mixins.early.minecraft.fastload.rand;

import java.util.Random;

import net.minecraft.world.gen.MapGenCaves;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mitchej123.hodgepodge.util.StdLCG;

@Mixin(MapGenCaves.class)
public class MixinMapGenCaves {

    @Unique
    private final StdLCG angelica$fastrand = new StdLCG();

    @WrapOperation(method = "func_151541_a", at = @At(value = "NEW", target = "(J)Ljava/util/Random;"))
    private Random angelica$fastrand(long seed, Operation<Random> original) {
        angelica$fastrand.setSeed(seed);
        return angelica$fastrand;
    }
}