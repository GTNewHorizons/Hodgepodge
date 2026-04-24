package com.mitchej123.hodgepodge.mixins.late.ic2;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import ic2.core.IC2;
import ic2.core.Ic2Items;
import ic2.core.Platform;
import ic2.core.item.ItemTinCan;
import ic2.core.util.StackUtil;

@Mixin(value = ItemTinCan.class)
public class MixinIc2TinCan {

    @Redirect(
            method = "onItemRightClick",
            at = @At(value = "INVOKE", target = "Lic2/core/Platform;isSimulating()Z", remap = false))
    public boolean hodgepodge$isSimulating(Platform instance) {
        // Allow onEaten to always run regardless of if we are on the client or the server
        return true;
    }

    /**
     * @author tiffit
     * @reason Add checks to only run some code on the server
     */
    @Overwrite(remap = false)
    public ItemStack onEaten(EntityPlayer player, ItemStack itemStack) {
        boolean isServer = IC2.platform.isSimulating();
        int needFood = 20 - player.getFoodStats().getFoodLevel();
        if (needFood > 0) {
            int toConsume = Math.min(needFood, itemStack.stackSize);
            if (StackUtil.storeInventoryItem(new ItemStack(Ic2Items.tinCan.getItem(), toConsume), player, true)) {
                player.getFoodStats().addStats(toConsume, (float) toConsume);
                if (isServer) {
                    itemStack.stackSize -= toConsume;
                    StackUtil.storeInventoryItem(new ItemStack(Ic2Items.tinCan.getItem(), toConsume), player, false);
                    IC2.platform.playSoundSp("Tools/eat.ogg", 1.0F, 1.0F);
                }
            }
        }

        return itemStack;
    }
}
