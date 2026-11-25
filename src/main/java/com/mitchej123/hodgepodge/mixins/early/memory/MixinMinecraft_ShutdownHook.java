package com.mitchej123.hodgepodge.mixins.early.memory;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mitchej123.hodgepodge.mixins.hooks.ClientLeaksCleaningHook;

@Mixin(Minecraft.class)
public class MixinMinecraft_ShutdownHook {

    @Inject(
            method = "loadWorld(Lnet/minecraft/client/multiplayer/WorldClient;Ljava/lang/String;)V",
            at = @At(
                    value = "INVOKE",
                    remap = false,
                    target = "Lcpw/mods/fml/client/FMLClientHandler;handleClientWorldClosing(Lnet/minecraft/client/multiplayer/WorldClient;)V",
                    shift = At.Shift.AFTER))
    private void runClientStopHook(WorldClient worldClientIn, String loadingMessage, CallbackInfo ci) {
        ClientLeaksCleaningHook.onClientExitToMainMenu((Minecraft) ((Object) this));
    }
}
