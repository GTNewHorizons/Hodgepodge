package com.mitchej123.hodgepodge.fixic2directinventoryaccess.mixins.core.item;

import ic2.core.item.ItemCropSeed;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ItemCropSeed.class)
public class MixinItemCropSeed {
    @Inject(method = "onItemUse", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/player/EntityPlayer;inventory:Lnet/minecraft/entity/player/InventoryPlayer;"), cancellable = true)
    public void onItemUse(ItemStack itemstack, EntityPlayer entityplayer, World world, int x, int y, int z, int side, float a, float b, float c, CallbackInfoReturnable<Boolean> ci) {
        entityplayer.inventory.setInventorySlotContents(entityplayer.inventory.currentItem, null);
        ci.setReturnValue(true);
        ci.cancel();
    }
}


// Alternative that doesn't use the remapping

//@Mixin(value = { ItemCropSeed.class}, remap = false)
//public class MixinItemCropSeed {
//    @Inject(method = 
//        "Lic2/core/item/ItemCropSeed;func_77648_a(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/world/World;IIIIFFF)Z"
//        , at = @At(value = "FIELD",
//        target = "Lnet/minecraft/entity/player/EntityPlayer;field_71071_by:Lnet/minecraft/entity/player/InventoryPlayer;"
//        ), cancellable=true
//    )
//    public void onItemUse(ItemStack itemstack, EntityPlayer entityplayer, World world, int x, int y, int z, int side, float a, float b, float c, CallbackInfoReturnable<Boolean> ci) {
//        System.out.println("Naughty naughty seedbag");
//        entityplayer.inventory.setInventorySlotContents(entityplayer.inventory.currentItem, null);
//        ci.setReturnValue(true);
//        ci.cancel();
//    }
//}
