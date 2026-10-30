package com.mitchej123.hodgepodge.mixins.early.minecraft;

import java.util.UUID;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = EntityArrow.class)
public abstract class MixinEntityArrow extends Entity {

    public MixinEntityArrow(World worldIn) {
        super(worldIn);
    }

    @Shadow
    public Entity shootingEntity;

    @Unique
    private UUID hodgepodge$cachedOwnerUUID;

    @Inject(method = "writeEntityToNBT", at = @At("TAIL"))
    public void hodgepodge$writeShooterData(NBTTagCompound tagCompound, CallbackInfo ci) {
        UUID uuid = this.shootingEntity != null ? this.shootingEntity.getUniqueID() : this.hodgepodge$cachedOwnerUUID;
        if (uuid != null) {
            tagCompound.setLong("OwnerUUIDMost", uuid.getMostSignificantBits());
            tagCompound.setLong("OwnerUUIDLeast", uuid.getLeastSignificantBits());
        }
    }

    @Inject(method = "readEntityFromNBT", at = @At("TAIL"))
    public void hodgepodge$readShooterData(NBTTagCompound tagCompound, CallbackInfo ci) {
        if (tagCompound.hasKey("OwnerUUIDMost", 99)) {
            this.hodgepodge$cachedOwnerUUID = new UUID(
                    tagCompound.getLong("OwnerUUIDMost"),
                    tagCompound.getLong("OwnerUUIDLeast"));
        }
    }

    @Inject(method = "onUpdate", at = @At("HEAD"))
    public void hodgepodge$onUpdate(CallbackInfo ci) {
        if (!this.worldObj.isRemote && this.shootingEntity == null && this.hodgepodge$cachedOwnerUUID != null) {
            EntityPlayer player = this.worldObj.func_152378_a(this.hodgepodge$cachedOwnerUUID);
            if (player != null) {
                this.shootingEntity = player;
                this.hodgepodge$cachedOwnerUUID = null;
            }

        }
    }
}