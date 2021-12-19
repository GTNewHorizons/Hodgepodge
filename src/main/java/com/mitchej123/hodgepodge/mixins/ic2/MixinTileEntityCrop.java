package com.mitchej123.hodgepodge.mixins.ic2;

import ic2.core.crop.TileEntityCrop;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = TileEntityCrop.class)
public class MixinTileEntityCrop {
    @Inject(method = "rightClick", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/player/EntityPlayer;inventory:Lnet/minecraft/entity/player/InventoryPlayer;"), cancellable = true)
    public void rightClick(EntityPlayer player, CallbackInfoReturnable<Boolean> ci) {
        player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
    }   
}
