package com.mitchej123.hodgepodge.mixins.late.voxelmap.reflection;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.renderer.entity.RenderManager;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.thevoxelbox.voxelmap.j;

@Mixin(j.class)
public class MixinMap {

    @Redirect(
            at = @At(
                    value = "INVOKE",
                    // Object getPrivateFieldValueByType(Object, Class<?>, Class<?>, int)
                    target = "Lcom/thevoxelbox/voxelmap/b/y;do(Ljava/lang/Object;Ljava/lang/Class;Ljava/lang/Class;I)Ljava/lang/Object;",
                    remap = false),
            // Map(IVoxelMap), void onTickInGame(Minecraft)
            method = { "<init>", "do(Lnet/minecraft/client/Minecraft;)V" },
            remap = false)
    private Object hodgepodge$getEntityRenderMap(Object var0, Class<?> var1, Class<?> var2, int var3) {
        return RenderManager.instance.entityRenderMap;
    }

    @Redirect(
            at = @At(
                    value = "INVOKE",
                    // Object getPrivateFieldValueByType(Object, Class<?>, Class<?>, int)
                    target = "Lcom/thevoxelbox/voxelmap/b/y;do(Ljava/lang/Object;Ljava/lang/Class;Ljava/lang/Class;I)Ljava/lang/Object;",
                    remap = false),
            // ?, void checkPermissionMessages()
            method = { "byte()V", "case()V" },
            remap = false)
    private Object hodgepodge$getChatLines(Object var0, Class<?> var1, Class<?> var2, int var3) {
        return ((GuiNewChat) var0).chatLines;
    }

    @Redirect(
            at = @At(
                    value = "INVOKE",
                    // Object getPrivateFieldValueByType(Object, Class<?>, Class<?>, int)
                    target = "Lcom/thevoxelbox/voxelmap/b/y;do(Ljava/lang/Object;Ljava/lang/Class;Ljava/lang/Class;I)Ljava/lang/Object;",
                    remap = false),
            // DynamicTexture getLightmapTexture()
            method = "int()Lnet/minecraft/client/renderer/texture/DynamicTexture;",
            remap = false)
    private Object hodgepodge$getLightmapTexture(Object var0, Class<?> var1, Class<?> var2, int var3) {
        return Minecraft.getMinecraft().entityRenderer.lightmapTexture;
    }
}
