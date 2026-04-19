package com.mitchej123.hodgepodge.mixins.early.memory;

import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.entity.RenderFallingBlock;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderFallingBlock.class)
public class MixinRenderFallingBlock {

    @Shadow
    @Final
    private RenderBlocks field_147920_a;

    @Inject(method = "doRender(Lnet/minecraft/entity/item/EntityFallingBlock;DDDFF)V", at = @At("RETURN"))
    private void clearRef(CallbackInfo ci) {
        this.field_147920_a.blockAccess = null;
    }
}
