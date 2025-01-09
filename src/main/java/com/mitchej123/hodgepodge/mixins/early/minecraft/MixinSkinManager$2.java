package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.client.resources.SkinManager;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.minecraft.client.resources.SkinManager$2")
public class MixinSkinManager$2 {

    @Shadow(aliases = "field_152636_b", remap = false)
    @Mutable
    @Final
    SkinManager.SkinAvailableCallback val$p_152789_3_;

    @SuppressWarnings("UnresolvedMixinReference")
    @Inject(
            method = "func_152634_a()V",
            at = @At(
                    value = "INVOKE",
                    target = "net/minecraft/client/resources/SkinManager$SkinAvailableCallback.func_152121_a(Lcom/mojang/authlib/minecraft/MinecraftProfileTexture$Type;Lnet/minecraft/util/ResourceLocation;)V",
                    shift = At.Shift.AFTER,
                    by = 1))
    private void hodgepodge$clearReference(CallbackInfo ignored) {
        val$p_152789_3_ = null;
    }
}
