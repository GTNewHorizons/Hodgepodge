package com.mitchej123.hodgepodge.mixins.late.witchery;

import net.minecraft.entity.player.EntityPlayer;

import org.spongepowered.asm.mixin.Mixin;

import com.emoniph.witchery.entity.EntityDemon;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;

@SuppressWarnings("UnusedMixin")
@Mixin(EntityDemon.class)
public abstract class MixinEntityDemon {

    @WrapMethod(method = "interact")
    private boolean hodgepodge$fixDemonShiftClick(EntityPlayer player, Operation<Boolean> original) {
        if (player.isSneaking()) return false;
        return original.call(player);
    }
}
