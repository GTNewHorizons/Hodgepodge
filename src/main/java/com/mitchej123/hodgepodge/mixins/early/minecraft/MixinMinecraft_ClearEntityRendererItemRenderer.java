package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.EntityRenderer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MixinMinecraft_ClearEntityRendererItemRenderer {

    @Inject(method = "loadWorld(Lnet/minecraft/client/multiplayer/WorldClient;Ljava/lang/String;)V", at = @At("TAIL"))
    private void hodgepodge$clearItemRenderer(WorldClient worldClient, String loadingMessage, CallbackInfo ci) {
        if (worldClient == null) {
            EntityRenderer entityRenderer = Minecraft.getMinecraft().entityRenderer;
            if (entityRenderer != null) {
                ((ItemRendererAccessor) entityRenderer.itemRenderer).setItemToRender(null);
            }
        }
    }
}
