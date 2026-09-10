package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityFurnace;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TileEntityFurnace.class)
public class MixinFurnaceSaveItemBurnTimeToNBT {

    @Shadow
    public int currentItemBurnTime;

    @Redirect(
            method = "readFromNBT",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/tileentity/TileEntityFurnace;getItemBurnTime(Lnet/minecraft/item/ItemStack;)I"
            )
    )
    private int readFromNBTItemBurnTime(ItemStack stack, NBTTagCompound compound) {
        return compound.getInteger("ItemBurnTime");
    }

    @Inject(method = "writeToNBT", at = @At("RETURN"))
    private void writeToNBTItemBurnTime(NBTTagCompound compound, CallbackInfo ci) {
        compound.setInteger("ItemBurnTime", this.currentItemBurnTime);
    }
}
