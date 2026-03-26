package com.mitchej123.hodgepodge.mixins.late.waila;

import net.minecraft.util.Vec3;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import com.mitchej123.hodgepodge.compat.WitcheryCompatBridge;

import mcp.mobius.waila.overlay.RayTracing;

@Mixin(RayTracing.class)
public class MixinRayTracingShapeshift {

    @ModifyVariable(method = "rayTrace", at = @At(value = "STORE", ordinal = 0), remap = false)
    private Vec3 modifyPos(Vec3 pos) {
        pos.yCoord -= WitcheryCompatBridge.yOffset;
        return pos;
    }
}
