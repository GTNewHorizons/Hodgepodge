package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.inventory.ContainerRepair;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import com.mitchej123.hodgepodge.config.TweaksConfig;

@Mixin(ContainerRepair.class)
public class MixinContainerRepair_MaxAnvilCost {

    @ModifyConstant(method = "updateRepairOutput", constant = @Constant(intValue = 40))
    private int getMaxAnvilCost(int original) {
        return TweaksConfig.anvilMaxLevel;
    }
}
