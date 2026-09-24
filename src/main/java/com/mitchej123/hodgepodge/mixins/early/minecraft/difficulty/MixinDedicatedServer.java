package com.mitchej123.hodgepodge.mixins.early.minecraft.difficulty;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.WorldServer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.mitchej123.hodgepodge.mixins.interfaces.IWorldDifficulty;

@Mixin(DedicatedServer.class)
public abstract class MixinDedicatedServer {

    @Inject(method = "func_147135_j", at = @At("HEAD"), cancellable = true)
    private void getWorldDifficulty(CallbackInfoReturnable<EnumDifficulty> cir) {
        WorldServer[] worlds = ((MinecraftServer) (Object) this).worldServers;
        if (worlds.length == 0 || worlds[0] == null) return;

        if (worlds[0].getWorldInfo() instanceof IWorldDifficulty worldDifficulty) {
            EnumDifficulty saved = worldDifficulty.hodgepodge$getDifficulty();
            if (saved != null) cir.setReturnValue(saved);
        }
    }
}
