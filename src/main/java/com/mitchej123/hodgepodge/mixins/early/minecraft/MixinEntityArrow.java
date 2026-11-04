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

    @Shadow
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

    /*
     * Called only when the arrow hits an entity. If injected elsewhere and it runs unconditionally, it may cause an
     * overhead as onUpdate is called for every loaded arrow per tick. The caveat is that this late injection makes the
     * self-fire prevention (ent != this.shootingEntity || this.ticksInAir >= 5) to always fail before the restoration,
     * which results in more situations where players get hit by arrows. But those are mostly when an arrow has just
     * unstucked out of a block / bounced off on an entity, and that seem natural to me.
     */
    @Inject(
            method = "onUpdate",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/MovingObjectPosition;<init>(Lnet/minecraft/entity/Entity;)V"))
    public void hodgepodge$restoreOwner(CallbackInfo ci) {
        this.hodgepodge$restoreOwner();
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

    private MixinEntityArrow(World worldIn) {
        super(worldIn);
    }
}
