package com.mitchej123.hodgepodge.mixins.early.minecraft.difficulty;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.EnumDifficulty;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

@Mixin(EntityPlayerMP.class)
public abstract class MixinEntityPlayerMP {

    @WrapOperation(
            method = "func_147100_a",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/MinecraftServer;func_147139_a(Lnet/minecraft/world/EnumDifficulty;)V"))
    private void ignoreLegacyClientDifficulty(MinecraftServer server, EnumDifficulty difficulty,
            Operation<Void> original) {}
}
