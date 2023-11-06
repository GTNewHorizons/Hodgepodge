package com.mitchej123.hodgepodge.mixins.late.voxelmap.reflection;

import net.minecraft.client.Minecraft;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.thevoxelbox.voxelmap.s;

@Mixin(s.class)
public class MixinVoxelMap {

    @Redirect(
            at = @At(
                    remap = false,
                    // Object getPrivateFieldValueByType(Object, Class<?>, Class<?>, int)
                    target = "Lcom/thevoxelbox/voxelmap/b/y;do(Ljava/lang/Object;Ljava/lang/Class;Ljava/lang/Class;I)Ljava/lang/Object;",
                    value = "INVOKE"),
            method = "do(ZZ)V", // void lateInit(boolean, boolean)
            remap = false)
    private Object hodgepodge$getResourcePacks(Object var0, Class<?> var1, Class<?> var2, int var3) {
        return Minecraft.getMinecraft().defaultResourcePacks;
    }
}
