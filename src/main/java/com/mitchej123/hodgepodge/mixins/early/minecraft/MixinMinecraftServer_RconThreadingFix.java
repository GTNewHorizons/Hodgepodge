package com.mitchej123.hodgepodge.mixins.early.minecraft;

import java.util.concurrent.ExecutionException;

import net.minecraft.command.ICommandManager;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.google.common.util.concurrent.ListenableFuture;
import com.gtnewhorizon.gtnhlib.util.ServerThreadUtil;

import cpw.mods.fml.common.FMLLog;

@Mixin(MinecraftServer.class)
public class MixinMinecraftServer_RconThreadingFix {

    @Redirect(
            method = "handleRConCommand",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/command/ICommandManager;executeCommand(Lnet/minecraft/command/ICommandSender;Ljava/lang/String;)I"))
    private int redirectExecuteCommand(ICommandManager instance, ICommandSender sender, String command) {
        ListenableFuture<Object> ret = ServerThreadUtil
                .callFromMainThread(() -> instance.executeCommand(sender, command));
        try {
            return (int) ret.get();
        } catch (InterruptedException | ExecutionException e) {
            FMLLog.severe(e.toString());
            return 0;
        }
    }

}
