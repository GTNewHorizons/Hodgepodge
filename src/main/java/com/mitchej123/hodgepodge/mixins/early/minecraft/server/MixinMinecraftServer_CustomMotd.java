package com.mitchej123.hodgepodge.mixins.early.minecraft.server;

import net.minecraft.network.ServerStatusResponse;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.IChatComponent;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.mitchej123.hodgepodge.config.TweaksConfig;
import com.mitchej123.hodgepodge.util.MOTDFormatter;

@Mixin(MinecraftServer.class)
public class MixinMinecraftServer_CustomMotd {

    @Inject(method = "func_147134_at", at = @At("RETURN"))
    private void hodgepodge$applyCustomMotd(CallbackInfoReturnable<ServerStatusResponse> cir) {
        if (!TweaksConfig.customMotdEnabled) {
            return;
        }

        ServerStatusResponse response = cir.getReturnValue();
        if (response != null) {
            MinecraftServer server = (MinecraftServer) (Object) this;
            IChatComponent customMotd = MOTDFormatter.buildMOTD(server);
            response.func_151315_a(customMotd); // setServerMotd
        }
    }
}
