package com.mitchej123.hodgepodge.mixins.early.ic2;

import net.minecraft.entity.player.EntityPlayer;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import ic2.core.crop.TileEntityCrop;

@Mixin(value = TileEntityCrop.class)
public class MixinTileEntityCrop {

    @Inject(
            method = "rightClick",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/entity/player/InventoryPlayer;mainInventory:[Lnet/minecraft/item/ItemStack;",
                    opcode = Opcodes.GETFIELD))
    public void hodgepodge$rightClick(EntityPlayer player, CallbackInfoReturnable<Boolean> ci) {
        player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
    }
}
