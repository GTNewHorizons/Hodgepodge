package com.mitchej123.hodgepodge.mixins.early.minecraft.difficulty;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.WorldServer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mitchej123.hodgepodge.mixins.interfaces.IWorldDifficulty;
import com.mitchej123.hodgepodge.net.MessageServerDifficulty;
import com.mitchej123.hodgepodge.net.NetworkHandler;

@Mixin(MinecraftServer.class)
public abstract class MixinMinecraftServer {

    @Shadow
    public WorldServer[] worldServers;

    @Inject(method = "func_147139_a", at = @At("TAIL"))
    private void syncDifficulty(EnumDifficulty difficulty, CallbackInfo ci) {
        if (this.worldServers.length == 0 || this.worldServers[0] == null) return;

        IWorldDifficulty info = (IWorldDifficulty) this.worldServers[0].getWorldInfo();
        EnumDifficulty saved = this.worldServers[0].difficultySetting;
        info.hodgepodge$setDifficulty(saved);

        for (WorldServer world : this.worldServers) {
            if (world != null) {
                NetworkHandler.instance.sendToDimension(
                        new MessageServerDifficulty(
                                world.difficultySetting,
                                saved,
                                info.hodgepodge$isDifficultyLocked()),
                        world.provider.dimensionId);
            }
        }
    }
}
