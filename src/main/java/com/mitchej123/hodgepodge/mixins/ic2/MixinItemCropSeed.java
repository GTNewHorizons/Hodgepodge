package com.mitchej123.hodgepodge.mixins.ic2;

import com.mitchej123.hodgepodge.Hodgepodge;
import ic2.core.init.InternalName;
import ic2.core.item.ItemCropSeed;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ItemCropSeed.class)
public class MixinItemCropSeed extends Item {
    @Inject(method = "onItemUse", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/player/EntityPlayer;inventory:Lnet/minecraft/entity/player/InventoryPlayer;"), cancellable = true)
    public void onItemUse(ItemStack itemstack, EntityPlayer entityplayer, World world, int x, int y, int z, int side, float a, float b, float c, CallbackInfoReturnable<Boolean> ci) {
        ItemStack currentItem = entityplayer.inventory.getCurrentItem();
        currentItem.stackSize -= 1;
        ci.setReturnValue(true);
        ci.cancel();
    }
    @Inject(at=@At("RETURN"), method="<init>(Lic2/core/init/InternalName;)V", remap = false)
    public void AdjustMaxStackSize(InternalName internalName, CallbackInfo ci) {
        final int maxStackSize = Hodgepodge.config.ic2SeedMaxStackSize;
        if(maxStackSize != 1) {
            Hodgepodge.log.info("Setting IC2 seed max stack size to " + maxStackSize);
            setMaxStackSize(maxStackSize);
        }
    }
}
