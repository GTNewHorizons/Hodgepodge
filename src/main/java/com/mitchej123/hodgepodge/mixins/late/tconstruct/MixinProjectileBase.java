package com.mitchej123.hodgepodge.mixins.late.tconstruct;

import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mitchej123.hodgepodge.mixins.interfaces.IArrowOwnerUUID;

import tconstruct.library.entity.ProjectileBase;

// Fix TiC projectile not saving its owner at all, thus being unable to restore its owner after the owner has been
// reloaded
@Mixin(ProjectileBase.class)
public abstract class MixinProjectileBase extends EntityArrow implements IArrowOwnerUUID {

    @Inject(method = "onHitEntity", at = @At("HEAD"), remap = false)
    public void hodgepodge$restoreOwner(CallbackInfo ci) {
        this.hodgepodge$restoreOwner();
    }

    @Override
    public boolean hodgepodge$allowEntityFallback() {
        return false; // Mobs cannot use TiC range weaponry
    }

    private MixinProjectileBase(World worldIn) {
        super(worldIn);
    }
}
