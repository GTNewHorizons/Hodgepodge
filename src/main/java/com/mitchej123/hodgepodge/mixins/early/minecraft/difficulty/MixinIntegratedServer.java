package com.mitchej123.hodgepodge.mixins.early.minecraft.difficulty;

import java.io.File;
import java.net.Proxy;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.WorldServer;

import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.sugar.Local;
import com.mitchej123.hodgepodge.mixins.interfaces.IWorldDifficulty;

@Mixin(IntegratedServer.class)
public abstract class MixinIntegratedServer extends MinecraftServer {

    @Shadow
    private Minecraft mc;

    @Shadow
    @Final
    private static Logger logger;

    public MixinIntegratedServer(File workDir, Proxy proxy) {
        super(workDir, proxy);
    }

    @Redirect(
            method = "loadAllWorlds",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/integrated/IntegratedServer;func_147139_a(Lnet/minecraft/world/EnumDifficulty;)V"))
    private void redirectInitialDifficultySet(IntegratedServer instance, EnumDifficulty difficulty,
            @Local WorldServer world) {
        final IWorldDifficulty overWorld = (IWorldDifficulty) world.getWorldInfo();

        final EnumDifficulty toApply = overWorld.getDifficulty() == null ? this.mc.gameSettings.difficulty
                : overWorld.getDifficulty();
        instance.func_147139_a(toApply);
    }

    @Override
    public void func_147139_a(EnumDifficulty difficulty) {
        super.func_147139_a(difficulty);
        // Read ACTUAL state after super
        WorldServer[] servers = this.worldServers;
        WorldServer server = this.worldServers[0];
        if (server == null || !(server.getWorldInfo() instanceof IWorldDifficulty info)) return;

        this.mc.gameSettings.difficulty = info.getDifficulty();

        // Sync client WorldInfo immediately without waiting for packet
        WorldClient worldClient = this.mc.theWorld;
        if (worldClient instanceof IWorldDifficulty worldDifficulty) {
            worldDifficulty.setDifficulty(info.getDifficulty());
            worldDifficulty.setDifficultyLocked(info.isDifficultyLocked());
        }
    }

    @Inject(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/MinecraftServer;tick()V",
                    shift = At.Shift.AFTER))
    private void onTick(CallbackInfo ci) {
        if (mc.theWorld == null || this.worldServers == null || this.worldServers[0] == null) return;
        final IWorldDifficulty serverPwd = (IWorldDifficulty) this.worldServers[0].getWorldInfo();
        if (!serverPwd.isDifficultyLocked() && mc.gameSettings.difficulty != serverPwd.getDifficulty()) {
            logger.info("Syncing difficulty to {}, from {}", mc.gameSettings.difficulty, serverPwd.getDifficulty());
            this.func_147139_a(mc.gameSettings.difficulty);
        }
    }
}
