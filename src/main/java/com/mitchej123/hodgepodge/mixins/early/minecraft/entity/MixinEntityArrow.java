package com.mitchej123.hodgepodge.mixins.early.minecraft.entity;

import java.util.UUID;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// Fix arrow not saving its owner at all, thus being unable to restore its owner after the owner has been reloaded
@Mixin(EntityArrow.class)
abstract class MixinEntityArrow extends Entity {

    @Shadow
    private Entity shootingEntity;

    @Unique
    private UUID hodgepodge$cachedOwnerUUID;

    @Inject(method = "writeEntityToNBT", at = @At("TAIL"))
    private void writeOwnerToNBT(NBTTagCompound tagCompound, CallbackInfo ci) {
        UUID uuid = shootingEntity != null ? shootingEntity.getUniqueID() : hodgepodge$cachedOwnerUUID;
        if (uuid != null) {
            tagCompound.setLong("OwnerUUIDMost", uuid.getMostSignificantBits());
            tagCompound.setLong("OwnerUUIDLeast", uuid.getLeastSignificantBits());
        }
    }

    @Inject(method = "readEntityFromNBT", at = @At("TAIL"))
    private void readOwnerFromNBT(NBTTagCompound tagCompound, CallbackInfo ci) {
        if (tagCompound.hasKey("OwnerUUIDMost", 99)) {
            hodgepodge$cachedOwnerUUID = new UUID(
                    tagCompound.getLong("OwnerUUIDMost"),
                    tagCompound.getLong("OwnerUUIDLeast"));
        }
    }

    /*
     * Called only when the arrow hits an entity. If injected elsewhere and it runs unconditionally, it may cause an
     * overhead as onUpdate is called for every loaded arrow per tick. The caveat is that this late injection makes the
     * self-fire prevention (ent != this.shootingEntity || this.ticksInAir >= 5) to always fail before the restoration,
     * which results in more situations where players get hit by arrows. But those are mostly when an arrow has just
     * unstucked out of a block / bounced off on an entity, and that seems natural to me.
     */
    @Inject(
            method = "onUpdate",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/MovingObjectPosition;<init>(Lnet/minecraft/entity/Entity;)V"))
    private void restoreOwner(CallbackInfo ci) {
        if (shootingEntity == null && hodgepodge$cachedOwnerUUID != null) {

            // 1. Search for the owner player
            Entity entity = this.worldObj.func_152378_a(hodgepodge$cachedOwnerUUID);

            // 2. If no player, search for the owner mob
            if (entity == null) {
                for (Entity ent : this.worldObj.loadedEntityList) {
                    if (ent instanceof EntityLivingBase && ent.getUniqueID().equals(hodgepodge$cachedOwnerUUID)) {
                        entity = ent;
                        break;
                    }
                }
            }

            if (entity != null) {
                shootingEntity = entity;
                hodgepodge$cachedOwnerUUID = null;
            }
        }
    }

    private MixinEntityArrow(World worldIn) {
        super(worldIn);
    }
}
