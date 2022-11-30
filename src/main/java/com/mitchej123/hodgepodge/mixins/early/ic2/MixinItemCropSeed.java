package com.mitchej123.hodgepodge.mixins.early.ic2;

import com.mitchej123.hodgepodge.Common;
import ic2.core.item.ItemCropSeed;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ItemCropSeed.class)
public class MixinItemCropSeed extends Item {

    @Inject(
            method = "onItemUse",
            at =
                    @At(
                            value = "FIELD",
                            target =
                                    "Lnet/minecraft/entity/player/EntityPlayer;inventory:Lnet/minecraft/entity/player/InventoryPlayer;"),
            cancellable = true)
    public void onItemUse(
            ItemStack itemstack,
            EntityPlayer entityplayer,
            World world,
            int x,
            int y,
            int z,
            int side,
            float a,
            float b,
            float c,
            CallbackInfoReturnable<Boolean> ci) {
        ItemStack currentItem = entityplayer.inventory.getCurrentItem();
        currentItem.stackSize -= 1;
        ci.setReturnValue(true);
        ci.cancel();
    }

    @ModifyConstant(constant = @Constant(intValue = 1), method = "<init>(Lic2/core/init/InternalName;)V", remap = false)
    private int getMaxStackSizeFromConfig(int original) {
        final int maxStackSize = Common.config.ic2SeedMaxStackSize;
        Common.log.info("Setting IC2 seed max stack size to " + maxStackSize);
        return maxStackSize;
    }
}
