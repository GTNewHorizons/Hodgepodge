package com.mitchej123.hodgepodge.mixins.early.minecraft.difficulty;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraft.world.WorldServer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mitchej123.hodgepodge.mixins.interfaces.IWorldDifficulty;
import com.mitchej123.hodgepodge.net.MessageServerDifficulty;
import com.mitchej123.hodgepodge.net.NetworkHandler;

@Mixin(ServerConfigurationManager.class)
public abstract class MixinServerConfigurationManager {

    // Vanilla calls this on login, respawn, and dimension changes.
    @Inject(method = "updateTimeAndWeatherForPlayer", at = @At("TAIL"))
    private void syncDifficulty(EntityPlayerMP player, WorldServer world, CallbackInfo ci) {
        if (world.getWorldInfo() instanceof IWorldDifficulty info) {
            NetworkHandler.instance.sendTo(
                    new MessageServerDifficulty(
                            world.difficultySetting,
                            info.hodgepodge$getDifficulty(),
                            info.hodgepodge$isDifficultyLocked()),
                    player);
        }
    }
}
