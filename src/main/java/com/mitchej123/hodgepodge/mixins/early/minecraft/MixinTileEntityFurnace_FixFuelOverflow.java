package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityFurnace;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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
     * Overwrites the getShort earlier in the method, additionally allows conversion of old short BurnTime to an Integer
     */
    @Inject(method = "readFromNBT", at = @At("RETURN"))
    private void hodgepodge$readBurnTime(NBTTagCompound compound, CallbackInfo ci) {
        if (compound.hasKey("BurnTime", 3)) {
            ((TileEntityFurnace) (Object) this).furnaceBurnTime = compound.getInteger("BurnTime");
        }
    }
}
