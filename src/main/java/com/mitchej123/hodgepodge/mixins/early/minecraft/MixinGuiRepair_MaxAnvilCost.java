package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.client.gui.GuiRepair;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import com.mitchej123.hodgepodge.config.TweaksConfig;

@Mixin(GuiRepair.class)
public class MixinGuiRepair_MaxAnvilCost {

    @ModifyConstant(method = "drawGuiContainerForegroundLayer", constant = @Constant(intValue = 40))
    private int getMaxAnvilCost(int original) {
        return TweaksConfig.anvilMaxLevel;
    }
}
