package com.mitchej123.hodgepodge.mixins.late.bibliocraft;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import jds.bibliocraft.tileentities.TileEntityArmorStand;

@Mixin(TileEntityArmorStand.class)
public class MixinTileEntityArmorStand_MarkDirty extends TileEntity {

    @Inject(method = "setInventorySlotContents", at = @At("TAIL"))
    private void triggerMarkDirty(int slot, ItemStack stack, CallbackInfo ci) {
        this.markDirty();
    }
}
