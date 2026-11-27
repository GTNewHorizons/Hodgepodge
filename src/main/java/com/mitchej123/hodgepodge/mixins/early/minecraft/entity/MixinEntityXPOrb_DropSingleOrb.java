package com.mitchej123.hodgepodge.mixins.early.minecraft.entity;

import net.minecraft.entity.item.EntityXPOrb;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(EntityXPOrb.class)
public abstract class MixinEntityXPOrb_DropSingleOrb {

    /**
     * @author nimuck
     * @reason Drop a single xp orb instead of subdividing into multiple. Capped at Short.MAX_VALUE because the orb
     *         value is stored as a short in NBT and in S11PacketSpawnExperienceOrb.
     */
    @Overwrite
    public static int getXPSplit(int expValue) {
        return expValue <= 0 ? 1 : Math.min(expValue, Short.MAX_VALUE);
    }
}
