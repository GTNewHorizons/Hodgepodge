package com.mitchej123.hodgepodge.mixins.late.voxelmap.reflection;

import net.minecraft.client.Minecraft;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.thevoxelbox.voxelmap.util.b;

@Mixin(b.class)
public class MixinAddonResourcePack {

    @Redirect(
            at = @At(
                    remap = false,
                    // Object getPrivateFieldValueByType(Object, Class<?>, Class<?>, int)
                    target = "Lcom/thevoxelbox/voxelmap/b/y;do(Ljava/lang/Object;Ljava/lang/Class;Ljava/lang/Class;I)Ljava/lang/Object;",
                    value = "INVOKE"),
            method = "<init>", // AddonResourcePack(String)
            remap = false)
    private Object hodgepodge$getFileAssets(Object var0, Class<?> var1, Class<?> var2, int var3) {
        return Minecraft.getMinecraft().fileAssets;
    }

}
