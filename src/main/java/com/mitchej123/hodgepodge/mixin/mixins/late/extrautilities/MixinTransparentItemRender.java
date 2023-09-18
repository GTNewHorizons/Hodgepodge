package com.mitchej123.hodgepodge.mixin.mixins.late.extrautilities;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;

import com.rwtema.extrautils.item.RenderItemMultiTransparency;
import com.rwtema.extrautils.item.RenderItemUnstable;

@Mixin({ RenderItemMultiTransparency.class, RenderItemUnstable.class })
public class MixinTransparentItemRender {

    @Redirect(
            method = "renderItem",
            remap = false,
            slice = @Slice(
                    from = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glBlendFunc(II)V"),
                    to = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glShadeModel(I)V")),
            at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glDisable(I)V"))
    private void hodgepodge$fixItemRender(int cap) {
        // do nothing to cancel call
    }
}
