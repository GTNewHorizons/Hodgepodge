package com.mitchej123.hodgepodge.mixins.late.thaumcraft;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import thaumcraft.api.WorldCoordinates;

@Mixin(value = WorldCoordinates.class, remap = false)
public class MixinWorldCoordinates {

    @Shadow
    public int x;

    @Shadow
    public int y;

    @Shadow
    public int z;

    @Shadow
    public int dim;

    /**
     * @author boubou19
     * @reason too many collisions with the default one
     */
    @Overwrite
    public int hashCode() {
        return this.x * 1217 + this.y * 127 + this.z * 2237 + this.dim * 31;
    }
}
