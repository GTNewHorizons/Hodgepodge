package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.client.Minecraft;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(Minecraft.class)
public class MixinMinecraft_FMLQueryFPS {

    @ModifyArg(
            method = "launchIntegratedServer(Ljava/lang/String;Ljava/lang/String;Lnet/minecraft/world/WorldSettings;)V",
            at = @At(value = "INVOKE", target = "Ljava/lang/Thread;sleep(J)V"))
    private long hodgepodge$raiseFMLQueryFPS(long millis) {
        return 34;
    }
}
