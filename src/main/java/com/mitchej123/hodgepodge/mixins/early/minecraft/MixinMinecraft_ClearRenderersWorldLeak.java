package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.renderer.RenderGlobal;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MixinMinecraft_ClearRenderersWorldLeak {

    @Shadow
    public EffectRenderer effectRenderer;

    @Shadow
    public RenderGlobal renderGlobal;

    @Inject(
            method = "loadWorld(Lnet/minecraft/client/multiplayer/WorldClient;Ljava/lang/String;)V",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/Minecraft;theWorld:Lnet/minecraft/client/multiplayer/WorldClient;",
                    opcode = Opcodes.PUTFIELD))
    private void hodgepodge$fixRenderersWorldLeak(WorldClient worldClient, String loadingMessage, CallbackInfo ci) {
        if (worldClient == null) {
            if (renderGlobal != null) {
                renderGlobal.setWorldAndLoadRenderers(null);
            }

            if (effectRenderer != null) {
                effectRenderer.clearEffects(null);
            }
        }
    }
}
