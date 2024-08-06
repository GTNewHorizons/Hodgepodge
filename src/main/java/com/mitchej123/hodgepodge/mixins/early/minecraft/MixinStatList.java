package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.entity.EntityList.EntityEggInfo;
import net.minecraft.stats.StatList;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mitchej123.hodgepodge.util.StatHandler;
import com.mitchej123.hodgepodge.util.StatHandler.EntityInfo;

@Mixin(StatList.class)
public class MixinStatList {

    @ModifyExpressionValue(
            at = @At(
                    target = "Lnet/minecraft/entity/EntityList;getStringFromID(I)Ljava/lang/String;",
                    value = "INVOKE"),
            method = { "func_151182_a", "func_151176_b" }) // these methods create and register the stats for
                                                           // killing/being killed by the specified entity
    private static String hodgepodge$getEntityName(String original, EntityEggInfo info) {
        if (info instanceof EntityInfo) {
            return StatHandler.currentEntityName;
        }
        return original;
    }

}
