package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.tileentity.TileEntityPiston;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;

@Mixin(TileEntityPiston.class)
public class MixinTileEntityPiston {

    @ModifyReturnValue(method = "getPistonOrientation", at = @At("RETURN"))
    private int hodgepodge$clampOrientation(int orientation) {
        return Math.floorMod(orientation, 6);
    }
}
