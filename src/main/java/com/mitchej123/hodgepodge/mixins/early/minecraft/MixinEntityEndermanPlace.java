package com.mitchej123.hodgepodge.mixins.early.minecraft;

import java.util.Random;

import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

@Mixin(EntityEnderman.class)
public abstract class MixinEntityEndermanPlace extends EntityMob {

    @Unique
    private static ItemStack[] hodgepodge$blacklist;

    public MixinEntityEndermanPlace(World p_i1738_1_) {
        super(p_i1738_1_);
    }

    @WrapOperation(
            method = "onLivingUpdate",
            at = @At(value = "INVOKE", target = "Ljava/util/Random;nextInt(I)I", remap = false, ordinal = 1))
    private int disableEndermanBlockPlace(Random instance, int i, Operation<Integer> original) {
        return Integer.MAX_VALUE;
    }
}
