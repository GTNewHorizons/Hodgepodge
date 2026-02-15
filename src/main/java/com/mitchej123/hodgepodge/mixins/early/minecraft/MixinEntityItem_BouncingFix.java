package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

@Mixin(Entity.class)
public class MixinEntityItem_BouncingFix {

    /**
     * @author Myask
     * @reason make items not bounce on stairs/cauldrons/etc. whose selection boxes are full, but collisions aren't
     * @param original o.return: whether the block has average selection bound box length >= 1
     * @return adjustment to ignore this for items
     */
    @ModifyExpressionValue(
            method = "func_145771_j",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;func_147469_q(III)Z", ordinal = 0))
    private boolean dontEjectBySelectionBox(boolean original) {
        return !((Object) this instanceof EntityItem);
    }
}
