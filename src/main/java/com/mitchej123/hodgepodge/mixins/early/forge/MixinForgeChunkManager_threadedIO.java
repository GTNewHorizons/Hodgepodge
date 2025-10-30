package com.mitchej123.hodgepodge.mixins.early.forge;

import java.io.File;
import java.io.IOException;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.ForgeChunkManager;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.mitchej123.hodgepodge.mixins.hooks.WorldDataSaverHook;

@Mixin(ForgeChunkManager.class)
public class MixinForgeChunkManager_threadedIO {

    @Redirect(
            method = "saveWorld",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/nbt/CompressedStreamTools;write(Lnet/minecraft/nbt/NBTTagCompound;Ljava/io/File;)V"))
    private static void redirectWrite(NBTTagCompound forcedChunkNBTData, File chunkLoaderFile) throws IOException {
        WorldDataSaverHook.saveWorldDataUncompressed(chunkLoaderFile, forcedChunkNBTData);
    }
}
