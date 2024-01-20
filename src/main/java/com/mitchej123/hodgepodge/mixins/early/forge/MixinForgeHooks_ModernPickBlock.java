package com.mitchej123.hodgepodge.mixins.early.forge;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;

@Mixin(value = ForgeHooks.class, remap = false)
public class MixinForgeHooks_ModernPickBlock {

    // credit for original mixin: bearsdotzone
    @Inject(method = "onPickBlock", at = @At(value = "RETURN", ordinal = 4), cancellable = true)
    private static void hodgepodge$onPickBlock(MovingObjectPosition target, EntityPlayer player, World world,
            CallbackInfoReturnable<Boolean> cir, @Local(name = "result") LocalRef<ItemStack> result) {
        Minecraft clientObject = Minecraft.getMinecraft();
        for (int x = 9; x < 36; x++) {
            ItemStack stack = player.inventory.getStackInSlot(x);
            if (stack != null && stack.isItemEqual(result.get())
                    && ItemStack.areItemStackTagsEqual(stack, result.get())) {
                int moveSlot = player.inventory.currentItem;
                moveSlot = 36 + moveSlot;

                clientObject.playerController.windowClick(player.inventoryContainer.windowId, x, 0, 0, player);
                clientObject.playerController.windowClick(player.inventoryContainer.windowId, moveSlot, 0, 0, player);
                clientObject.playerController.windowClick(player.inventoryContainer.windowId, x, 0, 0, player);
                cir.setReturnValue(true);
                return;
            }
        }
    }
}
