package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityFurnace;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.llamalad7.mixinextras.sugar.Local;

@Mixin(TileEntityFurnace.class)
public class MixinTileEntityFurnace_FixFuelOverflow {

    /**
     * Fixes BurnTime overflow with fuel over Short.MAX_VALUE BurnTime
     * <p>
     * Writes an Integer instead of a short for the BurnTime tag
     */
    @Redirect(
            method = "writeToNBT",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/nbt/NBTTagCompound;setShort(Ljava/lang/String;S)V",
                    ordinal = 0))
    private void hodgepodge$writeBurnTime(NBTTagCompound compound, String key, short value) {
        compound.setInteger(key, ((TileEntityFurnace) (Object) this).furnaceBurnTime);
    }

    /**
     * Fixes BurnTime overflow with fuel over Short.MAX_VALUE BurnTime
     * <p>
     * Overwrites the getShort with the getInteger value when it attempts to write the short value
     */
    @Redirect(
            method = "readFromNBT",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/tileentity/TileEntityFurnace;furnaceBurnTime:I",
                    opcode = Opcodes.PUTFIELD))
    private void hodgepodge$readBurnTime(TileEntityFurnace instance, int value,
            @Local(argsOnly = true) NBTTagCompound compound) {
        instance.furnaceBurnTime = compound.getInteger("BurnTime");
    }
}
