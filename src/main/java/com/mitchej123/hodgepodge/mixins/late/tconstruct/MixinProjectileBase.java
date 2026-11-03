package com.mitchej123.hodgepodge.mixins.late.tconstruct;

import java.util.UUID;

import net.minecraft.entity.Entity;
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
public abstract class MixinProjectileBase extends EntityArrow {

    private MixinProjectileBase(World worldIn) {
        super(worldIn);
    }

    @Inject(method = "onHitEntity", at = @At("HEAD"), remap = false)
    public void hodgepodge$restoreOwner(CallbackInfo ci) {
        IArrowOwnerUUID arrow = (IArrowOwnerUUID) this;
        UUID cachedUUID = arrow.hodgepodge$getCachedOwnerUUID();

        if (this.shootingEntity == null && cachedUUID != null) {
            // 1. Search for the owner player
            Entity entity = this.worldObj.func_152378_a(cachedUUID);

            // 2. If can't find a player, search for the general owner entity
            if (entity == null) {
                // I really wanted to use WorldServer.getEntityFromUuid() here, but it's 1.8+ only...
                for (Entity ent : this.worldObj.loadedEntityList) {
                    if (ent.getUniqueID().equals(cachedUUID)) {
                        entity = ent;
                        break;
                    }
                }
            }

            if (entity != null) {
                this.shootingEntity = entity;
                arrow.hodgepodge$setCachedOwnerUUID(null);
            }
        }
    }
}
