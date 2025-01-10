package com.mitchej123.hodgepodge.mixins.early.minecraft.server;

import net.minecraft.network.NetworkSystem;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.management.ServerConfigurationManager;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mitchej123.hodgepodge.Common;
import com.mitchej123.hodgepodge.mixins.interfaces.IPauseWhenEmpty;

@Mixin(MinecraftServer.class)
public abstract class MixinMinecraftServer_PauseWhenEmpty {

    @Shadow
    public abstract int getCurrentPlayerCount();

    @Shadow
    private ServerConfigurationManager serverConfigManager;

    @Shadow
    protected abstract void saveAllWorlds(boolean dontLog);

    @Shadow
    public abstract NetworkSystem func_147137_ag();

    @Unique
    private int hodgepodge$emptyTicks = 0;
    @Unique
    private boolean hodgepodge$wasPaused = false;

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true, order = 9000)
    public void hodgepodge$tick(CallbackInfo ci) {
        if ((Object) this instanceof DedicatedServer ds && ds instanceof IPauseWhenEmpty p) {
            int pauseTicks = p.getPauseWhenEmptySeconds() * 20;
            if (pauseTicks > 0) {
                if (this.getCurrentPlayerCount() == 0) {
                    this.hodgepodge$emptyTicks++;
                } else {
                    this.hodgepodge$emptyTicks = 0;
                }

                if (hodgepodge$emptyTicks >= pauseTicks) {
                    if (!hodgepodge$wasPaused) {
                        Common.log
                                .info("Server empty for {} seconds, saving and pausing", p.getPauseWhenEmptySeconds());
                        this.serverConfigManager.saveAllPlayerData();
                        this.saveAllWorlds(true);
                        hodgepodge$wasPaused = true;
                    }
                    // to finish saving chunks
                    net.minecraftforge.common.chunkio.ChunkIOExecutor.tick();
                    // to process new connections
                    this.func_147137_ag().networkTick();
                    // to process console commands
                    ds.executePendingCommands();
                    ci.cancel();
                } else if (hodgepodge$wasPaused) {
                    Common.log.info("Resuming server");
                    hodgepodge$wasPaused = false;
                }
            } else if (hodgepodge$wasPaused) {
                Common.log.info("Resuming server");
                hodgepodge$wasPaused = false;
            }
        }
    }
}
