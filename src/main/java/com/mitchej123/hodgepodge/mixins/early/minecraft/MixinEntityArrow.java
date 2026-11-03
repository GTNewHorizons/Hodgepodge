package com.mitchej123.hodgepodge.mixins.early.minecraft;

import java.util.UUID;

import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mitchej123.hodgepodge.mixins.interfaces.IArrowOwnerUUID;

// Fix arrow not saving its owner at all, thus being unable to restore its owner after the owner has been reloaded
@Mixin(EntityArrow.class)
public abstract class MixinEntityArrow extends Entity implements IArrowOwnerUUID {

    private MixinEntityArrow(World worldIn) {
        super(worldIn);
    }

    @Shadow
    public Entity shootingEntity;

    @Unique
    private UUID hodgepodge$cachedOwnerUUID;

    @Override
    public UUID hodgepodge$getCachedOwnerUUID() {
        return this.hodgepodge$cachedOwnerUUID;
    }

    @Override
    public void hodgepodge$setCachedOwnerUUID(UUID uuid) {
        this.hodgepodge$cachedOwnerUUID = uuid;
    }

    @Inject(method = "writeEntityToNBT", at = @At("TAIL"))
    public void hodgepodge$writeShooterUUID(NBTTagCompound tagCompound, CallbackInfo ci) {
        UUID uuid = this.shootingEntity != null ? this.shootingEntity.getUniqueID() : this.hodgepodge$cachedOwnerUUID;

        if (uuid != null) {
            tagCompound.setLong("OwnerUUIDMost", uuid.getMostSignificantBits());
            tagCompound.setLong("OwnerUUIDLeast", uuid.getLeastSignificantBits());
        }
    }

    // Don't attempt to resolve the owner here, as the owner may not be loaded yet
    @Inject(method = "readEntityFromNBT", at = @At("TAIL"))
    public void hodgepodge$readShooterUUID(NBTTagCompound tagCompound, CallbackInfo ci) {
        if (tagCompound.hasKey("OwnerUUIDMost", 99)) {
            this.hodgepodge$cachedOwnerUUID = new UUID(
                    tagCompound.getLong("OwnerUUIDMost"),
                    tagCompound.getLong("OwnerUUIDLeast"));
        }
    }

    /*
     * Called only when the arrow hits an entity; it's injected right after it passes the first hitEntity != null check.
     * If injected elsewhere and it runs unconditionally, it may cause an overhead as onUpdate is called every tick per
     * arrow. The caveat is that the safety check (ent != this.shootingEntity || this.ticksInAir >= 5), which is the
     * only point where restored shootingEntity won't be used, will always return true even if the ticksInAir < 5
     * (mostly when it's unstuck and falling out of the block / bounces off of an entity), but this seems natural to me.
     */
    @Inject(
            method = "onUpdate",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/projectile/EntityArrow;getIsCritical()Z",
                    ordinal = 0))
    public void hodgepodge$restoreOwner(CallbackInfo ci) {
        if (this.shootingEntity == null && this.hodgepodge$cachedOwnerUUID != null) {
            // 1. Search for the owner player
            Entity entity = this.worldObj.func_152378_a(this.hodgepodge$cachedOwnerUUID);

            // 2. If can't find a player, search for the general owner entity
            if (entity == null) {
                // I really wanted to use WorldServer.getEntityFromUuid() here, but it's 1.8+ only...
                for (Entity ent : this.worldObj.loadedEntityList) {
                    if (ent.getUniqueID().equals(this.hodgepodge$cachedOwnerUUID)) {
                        entity = ent;
                        break;
                    }
                }
            }

            if (entity != null) {
                this.shootingEntity = entity;
                this.hodgepodge$cachedOwnerUUID = null;
            }
        }
    }
}
