package com.mitchej123.hodgepodge.mixins.late.witchery;

import java.util.UUID;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.emoniph.witchery.entity.EntityBolt;
import com.emoniph.witchery.entity.EntityWitchHunter;
import com.mitchej123.hodgepodge.mixins.interfaces.IArrowOwnerUUID;

// Fix Witchery bolt not saving its owner at all, thus being unable to restore its owner after the owner has been
// reloaded
@Mixin(EntityBolt.class)
public abstract class MixinEntityBolt extends Entity implements IArrowOwnerUUID {

    @Shadow(remap = false)
    public Entity shootingEntity;

    @Unique
    private UUID hodgepodge$cachedOwnerUUID;

    @Inject(method = "writeEntityToNBT", at = @At("HEAD"))
    private void hodgepodge$writeOwnerToNBT(NBTTagCompound tagCompound, CallbackInfo ci) {
        this.hodgepodge$writeShooterUUID(tagCompound, this.hodgepodge$getShootingEntity());
    }

    @Inject(method = "readEntityFromNBT", at = @At("HEAD"))
    private void hodgepodge$readOwnerFromNBT(NBTTagCompound tagCompound, CallbackInfo ci) {
        this.hodgepodge$readShooterUUID(tagCompound);
    }

    @Inject(
            method = "onUpdate",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/MovingObjectPosition;<init>(Lnet/minecraft/entity/Entity;)V"))
    public void hodgepodge$restoreOwner(CallbackInfo ci) {
        this.hodgepodge$restoreOwner();
    }

    @Override
    public boolean hodgepodge$isValidFallbackEntity(Entity ent) {
        return ent instanceof EntityWitchHunter; // Assumes only EntityWitchHunter can shoot bolts
    }

    @Override
    public UUID hodgepodge$getCachedOwnerUUID() {
        return this.hodgepodge$cachedOwnerUUID;
    }

    @Override
    public void hodgepodge$setCachedOwnerUUID(UUID uuid) {
        this.hodgepodge$cachedOwnerUUID = uuid;
    }

    @Override
    public Entity hodgepodge$getShootingEntity() {
        return this.shootingEntity;
    }

    @Override
    public void hodgepodge$setShootingEntity(Entity entity) {
        this.shootingEntity = entity;
    }

    @Override
    public World hodgepodge$getWorldObj() {
        return this.worldObj;
    }

    private MixinEntityBolt(World worldIn) {
        super(worldIn);
    }
}
