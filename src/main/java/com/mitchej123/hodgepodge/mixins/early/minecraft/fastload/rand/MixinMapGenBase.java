package com.mitchej123.hodgepodge.mixins.early.minecraft.fastload.rand;

import java.util.Random;

import net.minecraft.world.gen.MapGenBase;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mitchej123.hodgepodge.util.StdLCG;

@Mixin(MapGenBase.class)
public class MixinMapGenBase {

    @WrapOperation(method = "<init>", at = @At(value = "NEW", target = "()Ljava/util/Random;"))
    private Random angelica$fasterRandom(Operation<Random> original) {
        return new StdLCG();
    }
}
