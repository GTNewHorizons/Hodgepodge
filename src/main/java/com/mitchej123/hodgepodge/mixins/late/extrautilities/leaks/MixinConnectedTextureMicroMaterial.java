package com.mitchej123.hodgepodge.mixins.late.extrautilities.leaks;

import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.rwtema.extrautils.block.render.RenderBlockConnectedTextures;
import com.rwtema.extrautils.multipart.ConnectedTextureMicroMaterial;

@Mixin(value = ConnectedTextureMicroMaterial.class, remap = false)
public class MixinConnectedTextureMicroMaterial {

    @Shadow
    private World world;

    @ModifyReturnValue(method = "renderConnected", at = @At(value = "RETURN", ordinal = 6))
    private boolean clearWorldAndFakeRender(boolean original) {
        this.world = null;
        RenderBlockConnectedTextures.fakeRender.setWorld(null);
        return original;
    }

    @ModifyReturnValue(
            method = "renderConnected",
            at = { @At(value = "RETURN", ordinal = 7), @At(value = "RETURN", ordinal = 8),
                    @At(value = "RETURN", ordinal = 9) })
    private boolean clearWorld(boolean original) {
        this.world = null;
        return original;
    }

}
