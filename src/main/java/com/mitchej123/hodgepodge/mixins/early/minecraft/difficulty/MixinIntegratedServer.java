package com.mitchej123.hodgepodge.mixins.early.minecraft.difficulty;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.WorldServer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.mitchej123.hodgepodge.mixins.interfaces.IWorldDifficulty;

@Mixin(IntegratedServer.class)
public abstract class MixinIntegratedServer {

    @Inject(method = "func_147135_j", at = @At("HEAD"), cancellable = true)
    private void getWorldDifficulty(CallbackInfoReturnable<EnumDifficulty> cir) {
        WorldServer[] worlds = ((MinecraftServer) (Object) this).worldServers;
        if (worlds.length == 0 || worlds[0] == null) return;

        EnumDifficulty saved = ((IWorldDifficulty) worlds[0].getWorldInfo()).hodgepodge$getDifficulty();
        cir.setReturnValue(saved == null ? EnumDifficulty.NORMAL : saved);
    }
}
