package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.entity.projectile.EntityEgg;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EntityEgg.class)
public abstract class MixinEntityEgg extends EntityThrowable {

    public MixinEntityEgg(World p_i1776_1_) {
        super(p_i1776_1_);
    }

    @Redirect(
            method = "onImpact",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;spawnParticle(Ljava/lang/String;DDDDDD)V"))
    private void replaceSpawnParticle(World world, String particleName, double x, double y, double z, double velocityX,
            double velocityY, double velocityZ) {
        worldObj.spawnParticle(
                "iconcrack_" + Item.getIdFromItem(Items.egg),
                x,
                y,
                z,
                ((double) rand.nextFloat() - 0.5D) * 0.08D,
                ((double) rand.nextFloat() - 0.5D) * 0.08D,
                ((double) rand.nextFloat() - 0.5D) * 0.08D);
    }
}
