package com.mitchej123.hodgepodge.mixins.early.minecraft.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityTrackerEntry;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.MathHelper;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

/** Uses live player coordinates for range checks when the server sends a chunk after a teleport. */
@Mixin(EntityTrackerEntry.class)
public class MixinEntityTrackerEntry_PlayerTrackingRange {

    @Unique
    private static final double hodgepodge$POSITION_SCALE = 32.0D;

    @Shadow
    public Entity myEntity;

    // The server can send the destination chunk before the tracker updates the encoded player position
    // Later reads must keep the encoded position for the spawn adjustment
    @ModifyExpressionValue(
            method = "tryStartWachingThis",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/entity/EntityTrackerEntry;lastScaledXPosition:I",
                    opcode = Opcodes.GETFIELD,
                    ordinal = 0))
    private int hodgepodge$useCurrentPlayerX(int encodedX) {
        return myEntity instanceof EntityPlayerMP ? MathHelper.floor_double(myEntity.posX * hodgepodge$POSITION_SCALE)
                : encodedX;
    }

    @ModifyExpressionValue(
            method = "tryStartWachingThis",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/entity/EntityTrackerEntry;lastScaledZPosition:I",
                    opcode = Opcodes.GETFIELD,
                    ordinal = 0))
    private int hodgepodge$useCurrentPlayerZ(int encodedZ) {
        return myEntity instanceof EntityPlayerMP ? MathHelper.floor_double(myEntity.posZ * hodgepodge$POSITION_SCALE)
                : encodedZ;
    }
}
