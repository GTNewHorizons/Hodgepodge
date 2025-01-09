package com.mitchej123.hodgepodge.mixins.early.minecraft.server;

import java.io.File;
import java.net.Proxy;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.dedicated.PropertyManager;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.mitchej123.hodgepodge.Common;

@Mixin(DedicatedServer.class)
public abstract class MixinMinecraftServer_PauseWhenEmpty extends MinecraftServer {

    @Shadow
    private PropertyManager settings;

    @Unique
    private int hodgepodge$pauseWhenEmptySeconds = 0;
    @Unique
    private int hodgepodge$emptyTicks = 0;

    public MixinMinecraftServer_PauseWhenEmpty(File workDir, Proxy proxy) {
        super(workDir, proxy);
    }

    @Inject(
            method = "startServer",
            at = @At(
                    value = "INVOKE",
                    target = "Lcpw/mods/fml/common/FMLCommonHandler;onServerStarted()V",
                    remap = false,
                    shift = At.Shift.AFTER))
    public void hodgepodge$setupServer(CallbackInfoReturnable<Boolean> cir) {
        hodgepodge$pauseWhenEmptySeconds = settings.getIntProperty("pause-when-empty-seconds", 0);
    }

    @Override
    public void tick() {
        int pauseTicks = hodgepodge$pauseWhenEmptySeconds * 20;
        if (pauseTicks > 0) {
            if (getCurrentPlayerCount() == 0) {
                this.hodgepodge$emptyTicks++;
            } else {
                this.hodgepodge$emptyTicks = 0;
            }

            if (hodgepodge$emptyTicks >= pauseTicks) {
                if (hodgepodge$emptyTicks == pauseTicks) {
                    Common.log
                            .info("Server empty for {} seconds, saving and pausing", hodgepodge$pauseWhenEmptySeconds);
                    this.serverConfigManager.saveAllPlayerData();
                    this.saveAllWorlds(true);
                }

                // to process new connections
                this.func_147137_ag().networkTick();
                return;
            }
        }

        super.tick();
    }
}
