package com.mitchej123.hodgepodge.mixins.late.extrautilities.leaks;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.rwtema.extrautils.block.render.FakeRenderBlocks;
import com.rwtema.extrautils.block.render.RenderBlockConnectedTextures;

@Mixin(RenderBlockConnectedTextures.class)
public abstract class MixinRenderBlockConnectedTextures {

    @Shadow
    public abstract FakeRenderBlocks getFakeRender();

    @ModifyReturnValue(method = "renderWorldBlock", at = @At(value = "RETURN", ordinal = 1), remap = false)
    private boolean clearWorld(boolean original) {
        this.getFakeRender().setWorld(null);
        return original;
    }

}
