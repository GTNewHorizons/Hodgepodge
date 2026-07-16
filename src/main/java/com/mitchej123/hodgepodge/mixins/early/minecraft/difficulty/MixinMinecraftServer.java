package com.mitchej123.hodgepodge.mixins.early.minecraft.difficulty;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.WorldServer;

import org.spongepowered.asm.lib.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mitchej123.hodgepodge.mixins.interfaces.IWorldDifficulty;
import com.mitchej123.hodgepodge.net.MessageServerDifficulty;
import com.mitchej123.hodgepodge.net.NetworkHandler;

@Mixin(MinecraftServer.class)
public abstract class MixinMinecraftServer {

    @WrapOperation(
            method = "func_147139_a",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/world/WorldServer;difficultySetting:Lnet/minecraft/world/EnumDifficulty;",
                    opcode = Opcodes.PUTFIELD))
    private void informPlayerDifficulty(WorldServer instance, EnumDifficulty value, Operation<Void> original) {
        if (instance.getWorldInfo() instanceof IWorldDifficulty worldDifficulty) {
            if (worldDifficulty.isDifficultyLocked()) return;
            original.call(instance, value);
            worldDifficulty.setDifficulty(value);
            NetworkHandler.instance
                    .sendToDimension(new MessageServerDifficulty(value, false), instance.provider.dimensionId);
        } else {
            original.call(instance, value);
        }
    }
}
