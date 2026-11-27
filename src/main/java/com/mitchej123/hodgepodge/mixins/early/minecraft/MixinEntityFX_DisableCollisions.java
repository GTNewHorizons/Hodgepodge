package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.client.particle.EntityFX;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;

@Mixin(EntityFX.class)
public abstract class MixinEntityFX_DisableCollisions extends Entity {

    /**
     * Disable collisions for all EntityFX particles for performance
     */
    @Override
    public void moveEntity(double x, double y, double z) {
        if (x != 0.0 || y != 0.0 || z != 0.0) {
            this.boundingBox.offset(x, y, z);
            this.posX = (this.boundingBox.minX + this.boundingBox.maxX) / 2.0;
            this.posY = this.boundingBox.minY + this.yOffset - this.ySize;
            this.posZ = (this.boundingBox.minZ + this.boundingBox.maxZ) / 2.0;
        }
    }

    private MixinEntityFX_DisableCollisions(World world) {
        super(world);
    }
}
