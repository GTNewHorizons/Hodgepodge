package com.mitchej123.hodgepodge.mixins.late.voxelmap.reflection;

import net.minecraft.client.resources.AbstractResourcePack;
import net.minecraft.client.resources.FallbackResourceManager;
import net.minecraft.client.resources.FileResourcePack;
import net.minecraft.client.resources.SimpleReloadableResourceManager;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.thevoxelbox.voxelmap.a;

@Mixin(a.class)
public class MixinColorManager {

    @Redirect(
            at = @At(
                    ordinal = 0,
                    remap = false,
                    // Object getPrivateFieldValueByType(Object, Class<?>, Class<?>, int)
                    target = "Lcom/thevoxelbox/voxelmap/b/y;do(Ljava/lang/Object;Ljava/lang/Class;Ljava/lang/Class;I)Ljava/lang/Object;",
                    value = "INVOKE"),
            // List<IResourcePack> findResources(String, String, String, boolean, boolean, boolean)
            method = "do(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;ZZZ)Ljava/util/List;",
            remap = false)
    private Object hodgepodge$getResourcePackZipFile(Object var0, Class<?> var1, Class<?> var2, int var3) {
        return ((FileResourcePack) var0).resourcePackZipFile;
    }

    @Redirect(
            at = @At(
                    ordinal = 1,
                    remap = false,
                    // Object getPrivateFieldValueByType(Object, Class<?>, Class<?>, int)
                    target = "Lcom/thevoxelbox/voxelmap/b/y;do(Ljava/lang/Object;Ljava/lang/Class;Ljava/lang/Class;I)Ljava/lang/Object;",
                    value = "INVOKE"),
            // List<IResourcePack> findResources(String, String, String, boolean, boolean, boolean)
            method = "do(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;ZZZ)Ljava/util/List;",
            remap = false)
    private Object hodgepodge$getResourcePackFile(Object var0, Class<?> var1, Class<?> var2, int var3) {
        return ((AbstractResourcePack) var0).resourcePackFile;
    }

    @Redirect(
            at = @At(
                    ordinal = 0,
                    remap = false,
                    // Object getPrivateFieldValueByType(Object, Class<?>, Class<?>, int)
                    target = "Lcom/thevoxelbox/voxelmap/b/y;do(Ljava/lang/Object;Ljava/lang/Class;Ljava/lang/Class;I)Ljava/lang/Object;",
                    value = "INVOKE"),
            // List<IResourcePack> getResourcePacks(String)
            method = "for(Ljava/lang/String;)Ljava/util/List;",
            remap = false)
    private Object hodgepodge$getDomainResourceManagers(Object var0, Class<?> var1, Class<?> var2, int var3) {
        return ((SimpleReloadableResourceManager) var0).domainResourceManagers;
    }

    @Redirect(
            at = @At(
                    ordinal = 1,
                    remap = false,
                    // Object getPrivateFieldValueByType(Object, Class<?>, Class<?>, int)
                    target = "Lcom/thevoxelbox/voxelmap/b/y;do(Ljava/lang/Object;Ljava/lang/Class;Ljava/lang/Class;I)Ljava/lang/Object;",
                    value = "INVOKE"),
            // List<IResourcePack> getResourcePacks(String)
            method = "for(Ljava/lang/String;)Ljava/util/List;",
            remap = false)
    private Object hodgepodge$getResourcePacks(Object var0, Class<?> var1, Class<?> var2, int var3) {
        return ((FallbackResourceManager) var0).resourcePacks;
    }
}
