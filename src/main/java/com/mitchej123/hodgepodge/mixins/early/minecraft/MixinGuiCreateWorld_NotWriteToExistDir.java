package com.mitchej123.hodgepodge.mixins.early.minecraft;

import java.io.File;

import net.minecraft.client.gui.GuiCreateWorld;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.SaveFormatOld;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GuiCreateWorld.class)
public abstract class MixinGuiCreateWorld_NotWriteToExistDir {

    @Unique
    private static String hodgepodge$handleConflict(ISaveFormat saveFormat, String worldName) {
        String dirName = worldName;
        int counter = 2;
        if (saveFormat instanceof SaveFormatOld sf) {
            while (new File(sf.savesDirectory, dirName).exists()) {
                dirName = worldName + " (" + counter + ")";
                counter++;
            }
        } else {
            while (saveFormat.getWorldInfo(dirName) != null) {
                dirName = worldName + " (" + counter + ")";
                counter++;
            }
        }
        return dirName;
    }

    @Inject(
            method = "func_146317_a(Lnet/minecraft/world/storage/ISaveFormat;Ljava/lang/String;)Ljava/lang/String;",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/storage/ISaveFormat;getWorldInfo(Ljava/lang/String;)Lnet/minecraft/world/storage/WorldInfo;"),
            cancellable = true)
    private static void hodgepodge$handleConflict1(ISaveFormat saveFormat, String worldName,
            CallbackInfoReturnable<String> cir) {
        cir.setReturnValue(hodgepodge$handleConflict(saveFormat, worldName));
    }

    @ModifyArg(
            method = "actionPerformed",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/Minecraft;launchIntegratedServer(Ljava/lang/String;Ljava/lang/String;Lnet/minecraft/world/WorldSettings;)V"),
            index = 0)
    private String hodgepodge$handleConflict2(String folderName) {
        return hodgepodge$handleConflict(((GuiScreen) (Object) this).mc.getSaveLoader(), folderName);
    }
}
