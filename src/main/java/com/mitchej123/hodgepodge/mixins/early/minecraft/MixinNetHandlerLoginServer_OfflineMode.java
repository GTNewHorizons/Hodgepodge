package com.mitchej123.hodgepodge.mixins.early.minecraft;

import java.util.Map;
import java.util.UUID;

import net.minecraft.server.network.NetHandlerLoginServer;
import net.minecraftforge.common.UsernameCache;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.mojang.authlib.GameProfile;

@Mixin(NetHandlerLoginServer.class)
public abstract class MixinNetHandlerLoginServer_OfflineMode {

    // If the player logged in before while online, they get the same UUID. Fixes issues with mods that store info
    // based on UUID (AE2 is a big one)
    @Inject(method = "func_152506_a", at = @At("HEAD"), cancellable = true)
    protected void func_152506_a(GameProfile original, CallbackInfoReturnable<GameProfile> cir) {
        Map<UUID, String> usernameCache = UsernameCache.getMap();
        for (Map.Entry<UUID, String> entry : usernameCache.entrySet()) {
            if (entry.getValue().equals(original.getName())) {
                cir.setReturnValue(new GameProfile(entry.getKey(), original.getName()));
            }
        }
    }

}
