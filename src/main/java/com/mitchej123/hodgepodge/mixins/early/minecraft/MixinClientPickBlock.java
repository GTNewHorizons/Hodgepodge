package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MixinClientPickBlock {

    @Inject(method = "func_147112_ai()V", at = @At("HEAD"))
    private void PickBlockReplacement(CallbackInfo info) {
        Minecraft clientObject = (Minecraft) (Object) this;
        MovingObjectPosition target = clientObject.objectMouseOver;
        EntityPlayer player = clientObject.thePlayer;
        World world = clientObject.theWorld;

        ItemStack result = null;
        boolean isCreative = player.capabilities.isCreativeMode;

        if (isCreative) {
            return;
        }

        if (target.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
            int x = target.blockX;
            int y = target.blockY;
            int z = target.blockZ;
            Block block = world.getBlock(x, y, z);

            if (block.isAir(world, x, y, z)) {
                return;
            }

            result = block.getPickBlock(target, world, x, y, z, player);
        } else {
            if (target.typeOfHit != MovingObjectPosition.MovingObjectType.ENTITY || target.entityHit == null) {
                return;
            }

            result = target.entityHit.getPickedResult(target);
        }

        if (result == null) {
            return;
        }

        for (int x = 0; x < 9; x++) {
            ItemStack stack = player.inventory.getStackInSlot(x);
            if (stack != null && stack.isItemEqual(result) && ItemStack.areItemStackTagsEqual(stack, result)) {
                player.inventory.currentItem = x;
                return;
            }
        }

        for (int x = 9; x < 36; x++) {
            ItemStack stack = player.inventory.getStackInSlot(x);
            if (stack != null && stack.isItemEqual(result) && ItemStack.areItemStackTagsEqual(stack, result)) {
                int moveSlot = player.inventory.currentItem;
                moveSlot = 36 + moveSlot;

                // Consider replacing this janky code with a packet, though that will require the change on both client
                // and server
                clientObject.playerController.windowClick(player.inventoryContainer.windowId, x, 0, 0, player);
                clientObject.playerController.windowClick(player.inventoryContainer.windowId, moveSlot, 0, 0, player);
                clientObject.playerController.windowClick(player.inventoryContainer.windowId, x, 0, 0, player);
                return;
            }
        }
    }
}
