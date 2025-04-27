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
import com.mitchej123.hodgepodge.config.TweaksConfig;

@Mixin(EntityEnderman.class)
public abstract class MixinEntityEndermanGrab extends EntityMob {

    @Unique
    private static ItemStack[] hodgepodge$blacklist;

    public MixinEntityEndermanGrab(World p_i1738_1_) {
        super(p_i1738_1_);
    }

    @WrapOperation(
            method = "onLivingUpdate",
            at = @At(value = "INVOKE", target = "Ljava/util/Random;nextInt(I)I", remap = false))
    private int checkEndermanBlockGrab(Random instance, int i, Operation<Integer> original) {
        if (TweaksConfig.endermanBlockGrabDisable) {
            return Integer.MAX_VALUE;
        }
        return original.call(instance, i);
    }
}
