package com.mitchej123.hodgepodge.mixins.interfaces;

import java.util.UUID;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public interface IArrowOwnerUUID {

    UUID hodgepodge$getCachedOwnerUUID();

    void hodgepodge$setCachedOwnerUUID(UUID uuid);

    Entity hodgepodge$getShootingEntity();

    void hodgepodge$setShootingEntity(Entity entity);

    World hodgepodge$getWorldObj();

    default void hodgepodge$writeShooterUUID(NBTTagCompound tagCompound, Entity shootingEntity) {
        UUID uuid = shootingEntity != null ? shootingEntity.getUniqueID() : this.hodgepodge$getCachedOwnerUUID();
        if (uuid != null) {
            tagCompound.setLong("OwnerUUIDMost", uuid.getMostSignificantBits());
            tagCompound.setLong("OwnerUUIDLeast", uuid.getLeastSignificantBits());
        }
    }

    default void hodgepodge$readShooterUUID(NBTTagCompound tagCompound) {
        if (tagCompound.hasKey("OwnerUUIDMost", 99)) {
            this.hodgepodge$setCachedOwnerUUID(
                    new UUID(tagCompound.getLong("OwnerUUIDMost"), tagCompound.getLong("OwnerUUIDLeast")));
        }
    }

    default boolean hodgepodge$allowEntityFallback() {
        return true;
    }

    default boolean hodgepodge$isValidFallbackEntity(Entity ent) {
        return ent instanceof net.minecraft.entity.EntityLivingBase;
    }

    default void hodgepodge$restoreOwner() {
        if (this.hodgepodge$getShootingEntity() == null && this.hodgepodge$getCachedOwnerUUID() != null) {
            World world = this.hodgepodge$getWorldObj();
            UUID uuid = this.hodgepodge$getCachedOwnerUUID();
            if (world == null || uuid == null) return;

            Entity entity = world.func_152378_a(uuid);

            if (entity == null && this.hodgepodge$allowEntityFallback()) {
                for (Entity ent : world.loadedEntityList) {
                    if (this.hodgepodge$isValidFallbackEntity(ent) && ent.getUniqueID().equals(uuid)) {
                        entity = ent;
                        break;
                    }
                }
            }

            if (entity != null) {
                this.hodgepodge$setShootingEntity(entity);
                this.hodgepodge$setCachedOwnerUUID(null);
            }
        }
    }
}
