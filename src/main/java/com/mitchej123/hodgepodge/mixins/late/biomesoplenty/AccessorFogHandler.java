package com.mitchej123.hodgepodge.mixins.late.biomesoplenty;

import biomesoplenty.client.fog.FogHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(FogHandler.class)
public interface AccessorFogHandler {

    @Accessor(remap = false)
    static double getFogX() {
        throw new IllegalStateException("Mixin stub invoked");
    }

    @Accessor(remap = false)
    static void setFogX(double fogX) {
        throw new IllegalStateException("Mixin stub invoked");
    }

    @Accessor(remap = false)
    static double getFogZ() {
        throw new IllegalStateException("Mixin stub invoked");
    }

    @Accessor(remap = false)
    static void setFogZ(double fogZ) {
        throw new IllegalStateException("Mixin stub invoked");
    }

    @Accessor(remap = false)
    static boolean isFogInit() {
        throw new IllegalStateException("Mixin stub invoked");
    }

    @Accessor(remap = false)
    static void setFogInit(boolean fogInit) {
        throw new IllegalStateException("Mixin stub invoked");
    }

    @Accessor(remap = false)
    static float getFogFarPlaneDistance() {
        throw new IllegalStateException("Mixin stub invoked");
    }

    @Accessor(remap = false)
    static void setFogFarPlaneDistance(float fogFarPlaneDistance) {
        throw new IllegalStateException("Mixin stub invoked");
    }

    @Invoker(remap = false)
    static void callRenderFog(int fogMode, float farPlaneDistance, float farPlaneDistanceScale) {
        throw new IllegalStateException("Mixin stub invoked");
    }
}
