package com.mitchej123.hodgepodge.mixins.early.minecraft;

import java.io.File;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.storage.SaveHandler;
import net.minecraft.world.storage.WorldInfo;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.sugar.Local;
import com.mitchej123.hodgepodge.mixins.hooks.WorldDataSaverHook;

@Mixin(SaveHandler.class)
public class MixinSaveHandler_threadedIO {

    @Inject(
            method = "saveWorldInfoWithPlayer",
            at = @At(
                    value = "INVOKE",
                    target = "Lcpw/mods/fml/common/FMLCommonHandler;handleWorldDataSave(Lnet/minecraft/world/storage/SaveHandler;Lnet/minecraft/world/storage/WorldInfo;Lnet/minecraft/nbt/NBTTagCompound;)V",
                    shift = At.Shift.AFTER,
                    remap = false),
            cancellable = true)
    private void injectSaveWorldDataWithPlayer(WorldInfo worldInfo, NBTTagCompound playerTag, CallbackInfo ci,
            @Local(ordinal = 2) NBTTagCompound nbttagcompound2) {
        File file = new File(((SaveHandler) (Object) this).getWorldDirectory(), "level.dat");
        WorldDataSaverHook.saveWorldDataBackup(file, nbttagcompound2);
        ci.cancel();
    }
}
