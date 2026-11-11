package com.mitchej123.hodgepodge.mixins.early.memory;

import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.entity.Entity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public class MixinEntityRenderer_ClearPointedEntity {

    @Shadow
    private Entity pointedEntity;

    @Inject(method = "getMouseOver", at = @At("RETURN"))
    private void clearPointedEntity(CallbackInfo ci) {
        this.pointedEntity = null;
    }
}
