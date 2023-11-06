package com.mitchej123.hodgepodge.mixins.late.voxelmap.reflection;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.FileResourcePack;
import net.minecraft.client.resources.FolderResourcePack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(targets = "com.thevoxelbox.voxelmap.u")
public class MixinWaypointManager$1 {

    @Redirect(
            at = @At(
                    remap = false,
                    // Object getPrivateFieldValueByType(Object, Class<?>, Class<?>, int)
                    target = "Lcom/thevoxelbox/voxelmap/b/y;do(Ljava/lang/Object;Ljava/lang/Class;Ljava/lang/Class;I)Ljava/lang/Object;",
                    value = "INVOKE"),
            method = "do(Lcom/thevoxelbox/voxelmap/c/h;)V", // void addIcons(TextureAtlas)
            remap = false)
    private Object hodgepodge$getDefaultResourcePacks(Object var0, Class<?> var1, Class<?> var2, int var3) {
        return Minecraft.getMinecraft().defaultResourcePacks;
    }

    @Redirect(
            at = @At(
                    remap = false,
                    // Object getPrivateFieldValueByType(Object, Class<?>, Class<?>, int)
                    target = "Lcom/thevoxelbox/voxelmap/b/y;do(Ljava/lang/Object;Ljava/lang/Class;Ljava/lang/Class;I)Ljava/lang/Object;",
                    value = "INVOKE"),
            // void addImagesFromFilePack(FileResourcePack var0, List<ResourceLocation> var1)
            method = "do(Lnet/minecraft/client/resources/FileResourcePack;Ljava/util/List;)V",
            remap = false)
    private static Object hodgepodge$getResourcePackZipFile(Object var0, Class<?> var1, Class<?> var2, int var3) {
        return ((FileResourcePack) var0).resourcePackZipFile;
    }

    @Redirect(
            at = @At(
                    remap = false,
                    // Object getPrivateFieldValueByType(Object, Class<?>, Class<?>, int)
                    target = "Lcom/thevoxelbox/voxelmap/b/y;do(Ljava/lang/Object;Ljava/lang/Class;Ljava/lang/Class;I)Ljava/lang/Object;",
                    value = "INVOKE"),
            // void addImagesFromFolderPack(FolderResourcePack var0, List<ResourceLocation> var1)
            method = "do(Lnet/minecraft/client/resources/FolderResourcePack;Ljava/util/List;)V",
            remap = false)
    private static Object hodgepodge$getResourcePackFile(Object var0, Class<?> var1, Class<?> var2, int var3) {
        return ((FolderResourcePack) var0).resourcePackFile;
    }

}
