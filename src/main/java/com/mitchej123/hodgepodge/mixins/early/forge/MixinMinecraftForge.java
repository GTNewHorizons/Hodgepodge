package com.mitchej123.hodgepodge.mixins.early.forge;

import net.minecraftforge.common.MinecraftForge;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(MinecraftForge.class)
public class MixinMinecraftForge {

    @ModifyConstant(
            constant = @Constant(stringValue = "net.minecraft.client.particle,EffectRenderer$1"),
            method = "initialize",
            remap = false)
    private static String hodgepodge$EffectRenderer$1(String original) {
        return "net.minecraft.client.particle.EffectRenderer$1";
    }

    @ModifyConstant(
            constant = @Constant(stringValue = "net.minecraft.client.particle,EffectRenderer$2"),
            method = "initialize",
            remap = false)
    private static String hodgepodge$EffectRenderer$2(String original) {
        return "net.minecraft.client.particle.EffectRenderer$2";
    }

    @ModifyConstant(
            constant = @Constant(stringValue = "net.minecraft.client.particle,EffectRenderer$3"),
            method = "initialize",
            remap = false)
    private static String hodgepodge$EffectRenderer$3(String original) {
        return "net.minecraft.client.particle.EffectRenderer$3";
    }

    @ModifyConstant(
            constant = @Constant(stringValue = "net.minecraft.client.particle,EffectRenderer$4"),
            method = "initialize",
            remap = false)
    private static String hodgepodge$EffectRenderer$4(String original) {
        return "net.minecraft.client.particle.EffectRenderer$4";
    }

}
