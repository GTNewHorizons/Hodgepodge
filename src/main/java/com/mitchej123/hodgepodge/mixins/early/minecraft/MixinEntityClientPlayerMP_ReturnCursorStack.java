package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mitchej123.hodgepodge.HodgepodgeEventHandler;
import com.mitchej123.hodgepodge.config.TweaksConfig;

@Mixin(EntityClientPlayerMP.class)
public abstract class MixinEntityClientPlayerMP_ReturnCursorStack {

    @Inject(method = "closeScreenNoPacket", at = @At("HEAD"))
    private void hodgepodge$returnCreativeCursorStack(CallbackInfo ci) {
        Minecraft mc = Minecraft.getMinecraft();
        EntityClientPlayerMP player = (EntityClientPlayerMP) (Object) this;
        if (!TweaksConfig.avoidDroppingItemsWhenClosing || !player.capabilities.isCreativeMode
                || !(mc.currentScreen instanceof GuiContainerCreative))
            return;

        ItemStack stack = player.inventory.getItemStack();
        if (stack == null) return;

        HodgepodgeEventHandler.returnStack(player, stack);
        player.inventory.setItemStack(null);
        player.inventoryContainer.detectAndSendChanges();
        if (stack.stackSize > 0) {
            mc.playerController.sendPacketDropItem(stack);
        }
    }
}
