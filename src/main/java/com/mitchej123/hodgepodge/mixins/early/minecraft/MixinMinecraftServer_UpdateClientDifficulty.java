package com.mitchej123.hodgepodge.mixins.early.minecraft;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mitchej123.hodgepodge.net.MessageChangeDifficulty;
import com.mitchej123.hodgepodge.net.NetworkHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.WorldServer;
import org.spongepowered.asm.lib.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(MinecraftServer.class)
public abstract class MixinMinecraftServer_UpdateClientDifficulty {

    @WrapOperation(method = "func_147139_a", at = @At(value = "FIELD", target = "Lnet/minecraft/world/WorldServer;difficultySetting:Lnet/minecraft/world/EnumDifficulty;", opcode = Opcodes.PUTFIELD))
    private void informPlayerDifficulty(WorldServer instance, EnumDifficulty value, Operation<Void> original) {
        original.call(instance, value);

        NetworkHandler.instance.sendToDimension(new MessageChangeDifficulty(value), instance.provider.dimensionId);
    }
}
